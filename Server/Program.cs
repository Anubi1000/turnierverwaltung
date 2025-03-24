using System.Net;
using System.Net.Sockets;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Text.Json.Serialization;
using FluentValidation;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Http.Json;
using Microsoft.AspNetCore.Server.Kestrel.Core;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Auth;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Results.Scoreboard;
using Turnierverwaltung.Server.Results.Word;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server;

public class Program
{
    public static void Main(string[] args)
    {
        ValidatorOptions.Global.LanguageManager.Enabled = false;

        var builder = WebApplication.CreateBuilder(args);

        // Create temporary logger until dependency container is ready
        using (
            var loggerFactory = LoggerFactory.Create(logBuilder =>
                logBuilder.AddConfiguration(builder.Configuration.GetSection("Logging")).AddConsole()
            )
        )
        {
            var logger = loggerFactory.CreateLogger<Program>();

            // Setup user data service
            var dataService = UserDataService.CreateNew();
            logger.LogInformation("Current data directory: {}", dataService.DataDirectory);
            builder.Services.AddSingleton<IUserDataService>(dataService);

            // Add user config file if it exists
            var userConfigFile = dataService.GetUserDataPath(UserDataType.Config);
            if (File.Exists(userConfigFile))
            {
                builder.Configuration.AddJsonFile(userConfigFile, false, false);
                logger.LogInformation("Found user config file at {}", userConfigFile);
            }

            logger.LogInformation("Configuring services");
            ConfigureServices(builder);
            SetupHttps(builder, dataService, logger);
        }

        var app = builder.Build();

        using (var scope = app.Services.CreateScope())
        {
            var logger = scope.ServiceProvider.GetRequiredService<ILogger<Program>>();
            logger.LogInformation("Running database migrations");

            var dbContext = scope.ServiceProvider.GetRequiredService<ApplicationDbContext>();
            dbContext.Database.Migrate();
        }

        if (app.Environment.IsDevelopment())
        {
#if DEBUG
            app.MapOpenApi();
            app.UseSwaggerUI(options => options.SwaggerEndpoint("/openapi/v1.json", "Turnierverwaltung"));
#endif
            app.UseDeveloperExceptionPage();
        }

        app.UseAuthentication();
        app.UseAuthorization();

        app.MapGroup("/").WithTags("Util").MapUtilEndpoints();

        app.MapTournamentEndpoints();
        app.MapOverviewEndpoints();
        app.MapClubEndpoints();
        app.MapDisciplineEndpoints();
        app.MapParticipantEndpoints();
        app.MapParticipantResultEndpoints();
        app.MapTeamDisciplineEndpoints();

#if DEBUG
        app.MapReverseProxy();
#else
        app.UseDefaultFiles();
        app.MapStaticAssets();
        app.MapFallbackToFile("index.html");
#endif

        app.Run();
    }

    private static void ConfigureServices(WebApplicationBuilder builder)
    {
        var services = builder.Services;
        if (builder.Environment.IsDevelopment())
            services.AddAppApiDoc();

        services.AddScoped<IScoreboardDataCreator, ScoreboardDataCreator>();
        services.AddScoped<IWordFileCreator, WordFileCreator>();

        services.AddValidatorsFromAssemblyContaining<Program>();

        ConfigureDatabases(services);
        ConfigureJsonSerialisation(services);
        ConfigureAuthentication(services);

#if DEBUG
        services.AddReverseProxy().LoadFromConfig(builder.Configuration.GetSection("ReverseProxy"));
#endif
    }

    private static void ConfigureDatabases(IServiceCollection services)
    {
        services.AddDbContext<ApplicationDbContext>(
            (serviceProvider, options) =>
            {
                var dataService = serviceProvider.GetRequiredService<IUserDataService>();
                options.UseSqlite($"Data Source={dataService.GetUserDataPath(UserDataType.Database)}");
            }
        );
    }

    private static void ConfigureJsonSerialisation(IServiceCollection services)
    {
        services.Configure<JsonOptions>(options =>
        {
            options.SerializerOptions.Converters.Add(new DateTimeToTimestampConverter());
            options.SerializerOptions.Converters.Add(new JsonStringEnumConverter());
            options.SerializerOptions.TypeInfoResolverChain.Insert(0, AppJsonSerializerContext.Default);
        });
    }

    private static void ConfigureAuthentication(IServiceCollection services)
    {
        services
            .AddAuthentication("localhost")
            .AddScheme<AuthenticationSchemeOptions, LocalhostAuthenticationHandler>("localhost", _ => { });
        services.AddAuthorization();
    }

    private static void SetupHttps(WebApplicationBuilder builder, UserDataService dataService, ILogger<Program> logger)
    {
        if (builder.Configuration.GetValue<bool>("DisableInbuiltCertificate") || builder.Environment.IsDevelopment())
            return;

        var path = dataService.GetUserDataPath(UserDataType.CertificateWithKey);
        logger.LogInformation("Using certificate at {} for https", path);

        if (!File.Exists(path))
        {
            logger.LogInformation("Generating new https certificate");

            var cert = CreateSelfSignedCertificate();
            File.WriteAllBytes(path, cert.Export(X509ContentType.Pfx));

            File.WriteAllText(
                dataService.GetUserDataPath(UserDataType.Certificate),
                "-----BEGIN CERTIFICATE-----\r\n"
                    + Convert.ToBase64String(
                        cert.Export(X509ContentType.Cert),
                        Base64FormattingOptions.InsertLineBreaks
                    )
                    + "\r\n-----END CERTIFICATE-----"
            );
        }

        builder.WebHost.ConfigureKestrel(options =>
        {
            var certPath = dataService.GetUserDataPath(UserDataType.CertificateWithKey);
            options.Listen(
                IPAddress.Any,
                443,
                listenOptions =>
                {
                    listenOptions.UseHttps(certPath);
                    listenOptions.Protocols = HttpProtocols.Http1AndHttp2AndHttp3;
                }
            );
        });
    }

    private static X509Certificate2 CreateSelfSignedCertificate()
    {
        var localIps = Dns.GetHostEntry(Dns.GetHostName())
            .AddressList.Where(ip => ip.AddressFamily is AddressFamily.InterNetwork or AddressFamily.InterNetworkV6)
            .ToList();

        using var rsa = RSA.Create();
        var request = new CertificateRequest(
            "CN=Turnierverwaltung",
            rsa,
            HashAlgorithmName.SHA256,
            RSASignaturePadding.Pkcs1
        );

        var sanBuilder = new SubjectAlternativeNameBuilder();
        sanBuilder.AddDnsName("localhost");

        foreach (var ip in localIps)
            sanBuilder.AddIpAddress(ip);

        if (!localIps.Contains(IPAddress.Loopback))
            sanBuilder.AddIpAddress(IPAddress.Loopback);

        if (!localIps.Contains(IPAddress.IPv6Loopback))
            sanBuilder.AddIpAddress(IPAddress.IPv6Loopback);

        request.CertificateExtensions.Add(sanBuilder.Build());
        request.CertificateExtensions.Add(
            new X509KeyUsageExtension(X509KeyUsageFlags.DigitalSignature | X509KeyUsageFlags.KeyEncipherment, true)
        );
        request.CertificateExtensions.Add(
            new X509EnhancedKeyUsageExtension(new OidCollection { new Oid("1.3.6.1.5.5.7.3.1") }, true)
        );

        var cert = request.CreateSelfSigned(DateTimeOffset.Now, DateTimeOffset.Now.AddYears(10));

        return cert;
    }
}

using System.Text.Json.Serialization;
using FluentValidation;
using Microsoft.AspNetCore.Authentication;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Auth;
using Turnierverwaltung.Server.Config;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Notification;
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

        var builder = WebApplication.CreateSlimBuilder(args);

        // Kestrel
        builder.WebHost.UseKestrelHttpsConfiguration();
        builder.WebHost.ConfigureKestrel(options =>
        {
            options.AddServerHeader = false;
        });

        // UserDataService
        var userDataService = UserDataService.CreateNew();
        builder.Services.AddSingleton<IUserDataService>(userDataService);

        AppConfig.SetupFile(userDataService.GetUserDataPath(UserDataType.Config));
        builder.Configuration.AddJsonFile(userDataService.GetUserDataPath(UserDataType.Config), optional: true);
        builder.Services.Configure<AppConfig>(builder.Configuration.GetSection("AppSettings"));

#if !RELEASEOPTIMIZED
        if (builder.Environment.IsDevelopment())
            builder.Services.AddAppApiDoc();
#endif

        builder.Services.AddScoped<IScoreboardDataCreator, ScoreboardDataCreator>();
        builder.Services.AddScoped<IWordFileCreator, WordFileCreator>();

        builder.Services.AddScoped<IEntityChangeNotifier, EntityChangeNotifier>();
        builder.Services.AddScoped<IScoreboardManager, ScoreboardManager>();

        // Validators
        builder.Services.AddValidatorsFromAssemblyContaining<Program>();

        // Database
        builder.Services.AddDbContext<ApplicationDbContext>(
            (serviceProvider, options) =>
            {
                var dataService = serviceProvider.GetRequiredService<IUserDataService>();
                options.UseSqlite($"Data Source={dataService.GetUserDataPath(UserDataType.Database)}");
            }
        );

        // Json serialisation
        builder.Services.ConfigureHttpJsonOptions(options =>
        {
            options.SerializerOptions.Converters.Add(new DateTimeToTimestampConverter());
            options.SerializerOptions.Converters.Add(new JsonStringEnumConverter());
            options.SerializerOptions.TypeInfoResolverChain.Insert(0, AppJsonSerializerContext.Default);
        });

        // Authentication
        builder
            .Services.AddAuthentication("localhost_auth")
            .AddScheme<AuthenticationSchemeOptions, LocalhostAuthenticationHandler>("localhost_auth", _ => { });
        builder.Services.AddAuthorization();

        // SignalR
        builder
            .Services.AddSignalR()
            .AddJsonProtocol(options =>
            {
                options.PayloadSerializerOptions.TypeInfoResolverChain.Insert(0, AppJsonSerializerContext.Default);
            });

        var app = builder.Build();

        app.Logger.LogInformation("Data Directory: {}", userDataService.DataDirectory);

        // Database migrations
        using (var scope = app.Services.CreateScope())
        {
            app.Logger.LogInformation("Running database migrations");
            var dbContext = scope.ServiceProvider.GetRequiredService<ApplicationDbContext>();
            dbContext.Database.Migrate();
        }

        // Development pages
#if !RELEASEOPTIMIZED
        if (app.Environment.IsDevelopment())
        {
            app.MapOpenApi();
            app.UseSwaggerUI(options => options.SwaggerEndpoint("/openapi/v1.json", "Turnierverwaltung"));
            app.UseDeveloperExceptionPage();
        }
#endif

        app.UseAuthentication();
        app.UseAuthorization();

        // Map endpoints
        app.MapUtilEndpoints();
        app.MapTournamentEndpoints();
        app.MapOverviewEndpoints();
        app.MapClubEndpoints();
        app.MapDisciplineEndpoints();
        app.MapParticipantEndpoints();
        app.MapParticipantResultEndpoints();
        app.MapTeamDisciplineEndpoints();
        app.MapTeamEndpoints();
        app.MapScoreboardEndpoints();

        // Map static files
        app.UseStaticFiles(
            new StaticFileOptions
            {
                OnPrepareResponse = context =>
                {
                    if (context.Context.Response.ContentType != MimeTypes.Html)
                        context.Context.Response.Headers.CacheControl = "public, max-age=31536000, immutable";
                },
            }
        );
        app.MapFallbackToFile("index.html");

        app.Run();
    }
}

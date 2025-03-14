using System.Reflection;
using System.Text.Json.Serialization;
using FluentValidation;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Http.Json;
using Microsoft.AspNetCore.OpenApi;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi.Models;
using Turnierverwaltung.Server.Auth;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Model.Transfer;
using Turnierverwaltung.Server.Model.Transfer.Club;
using Turnierverwaltung.Server.Model.Transfer.Discipline;
using Turnierverwaltung.Server.Model.Transfer.Participant;
using Turnierverwaltung.Server.Model.Transfer.TeamDiscipline;
using Turnierverwaltung.Server.Model.Transfer.Tournament;
using Turnierverwaltung.Server.Model.Validation;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server;

public static class Program
{
    public static void Main(string[] args)
    {
        ValidatorOptions.Global.LanguageManager.Enabled = false;

        var builder = WebApplication.CreateBuilder(args);
        ConfigureServices(builder.Services, builder);

        var app = builder.Build();

        using (var scope = app.Services.CreateScope())
        {
            var dbContext = scope.ServiceProvider.GetRequiredService<ApplicationDbContext>();
            dbContext.Database.Migrate();
        }

        if (app.Environment.IsDevelopment())
        {
            app.MapOpenApi();
            app.UseDeveloperExceptionPage();
        }

        app.UseAuthentication();
        app.UseAuthorization();

        //app.UseHttpsRedirection();

        app.MapGroup("/").WithTags("Util").MapUtilEndpoints();

        app.MapTournamentEndpoints();
        app.MapOverviewEndpoints();
        app.MapClubEndpoints();
        app.MapDisciplineEndpoints();
        app.MapParticipantEndpoints();
        app.MapTeamDisciplineEndpoints();

#if DEBUG
        app.MapReverseProxy();
#endif

        app.UseDefaultFiles();
        app.MapStaticAssets();
        app.MapFallbackToFile("/index.html");

        app.Run();
    }

    private static void ConfigureServices(IServiceCollection services, WebApplicationBuilder builder)
    {
        // Add OpenApi document generation
        AddApiDoc(services);

        // Add auto validation
        services.AddScoped<IValidator<TournamentEditDto>, TournamentEditDtoValidator>();
        services.AddScoped<IValidator<ClubEditDto>, ClubEditDtoValidator>();
        services.AddScoped<IValidator<DisciplineEditDto>, DisciplineEditDtoValidator>();
        services.AddScoped<IValidator<ParticipantEditDto>, ParticipantEditDtoValidator>();
        services.AddScoped<IValidator<ParticipantResultEditDto>, ParticipantResultEditDtoValidator>();
        services.AddScoped<IValidator<TeamDisciplineEditDto>, TeamDisciplineEditDtoValidator>();
        services.AddScoped<IValidator<WordDocGenerationDto>, WordDocGenerationDtoValidator>();

        // Add database context
        var connectionString = GetDbPath();
        services.AddDbContext<ApplicationDbContext>(options =>
        {
            options.UseSqlite(connectionString);
        });

        // Add DateTime to timestamp converter
        services.Configure<JsonOptions>(options =>
        {
            options.SerializerOptions.Converters.Add(new DateTimeToTimestampConverter());
            options.SerializerOptions.Converters.Add(new JsonStringEnumConverter());
        });

        // Add localhost auth
        services
            .AddAuthentication("localhost")
            .AddScheme<AuthenticationSchemeOptions, LocalhostAuthenticationHandler>("localhost", _ => { });
        services.AddAuthorization();

#if DEBUG
        services.AddReverseProxy().LoadFromConfig(builder.Configuration.GetSection("ReverseProxy"));
#endif
    }

    private static string GetDbPath()
    {
        var dir = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
        if (dir == "")
            dir = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
        if (dir == null)
            throw new DirectoryNotFoundException("Could not find suitable data dir");

        dir += "/Turnierverwaltung";

        Directory.CreateDirectory(dir);

        return $"Data Source={dir}/Data.db";
    }

    private static void AddApiDoc(IServiceCollection services)
    {
        services.AddOpenApi(options =>
        {
            options.AddDocumentTransformer(
                (document, context, cancellationToken) =>
                {
                    document.Info = new OpenApiInfo { Title = "Turnierverwaltung.Server", Version = "1.0.0" };

                    return Task.CompletedTask;
                }
            );

            options.CreateSchemaReferenceId = jsonTypeInfo =>
            {
                var type = jsonTypeInfo.Type;
                if (!type.IsNested)
                    return OpenApiOptions.CreateDefaultSchemaReferenceId(jsonTypeInfo);

                if (type.DeclaringType is not null)
                {
                    var attributes = type.DeclaringType.GetCustomAttributes<JsonDerivedTypeAttribute>();
                    if (attributes.Any())
                        return OpenApiOptions.CreateDefaultSchemaReferenceId(jsonTypeInfo);
                }

                return GetFullName(type);

                string GetFullName(Type type)
                {
                    if (type.DeclaringType == null)
                        return type.Name;
                    return GetFullName(type.DeclaringType) + "_" + type.Name;
                }
            };

            options.AddSchemaTransformer(
                (schema, context, cancellationToken) =>
                {
                    if (
                        context.JsonTypeInfo.Type == typeof(DateTime)
                        && context.JsonTypeInfo.Converter is DateTimeToTimestampConverter
                    )
                    {
                        schema.Format = "int64";
                        schema.Type = "integer";
                    }

                    return Task.CompletedTask;
                }
            );
        });
    }
}

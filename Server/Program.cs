using System.Text.Json.Serialization;
using FluentValidation;
using Microsoft.AspNetCore.Authentication;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Auth;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Model.Transfer;
using Turnierverwaltung.Server.Model.Transfer.Club;
using Turnierverwaltung.Server.Model.Transfer.Discipline;
using Turnierverwaltung.Server.Model.Transfer.Participant;
using Turnierverwaltung.Server.Model.Transfer.Team;
using Turnierverwaltung.Server.Model.Transfer.TeamDiscipline;
using Turnierverwaltung.Server.Model.Transfer.Tournament;
using Turnierverwaltung.Server.Model.Validation;
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
        builder.Services.AddSingleton(userDataService);

        var config = userDataService.LoadConfig();
        builder.Services.AddSingleton(config);

#if DEBUG
        if (builder.Environment.IsDevelopment())
            builder.Services.AddAppApiDoc();
#endif

        builder.Services.AddScoped<IScoreboardDataCreator, ScoreboardDataCreator>();
        builder.Services.AddScoped<IWordFileCreator, WordFileCreator>();

        builder.Services.AddSingleton<IScoreboardManager, ScoreboardManager>();

        // Validators
        builder.Services.AddScoped<IValidator<ClubEditDto>, ClubEditDtoValidator>();
        builder.Services.AddScoped<IValidator<DisciplineEditDto>, DisciplineEditDtoValidator>();
        builder.Services.AddScoped<IValidator<ParticipantEditDto>, ParticipantEditDtoValidator>();
        builder.Services.AddScoped<IValidator<ParticipantResultEditDto>, ParticipantResultEditDtoValidator>();
        builder.Services.AddScoped<IValidator<TeamDisciplineEditDto>, TeamDisciplineEditDtoValidator>();
        builder.Services.AddScoped<IValidator<TeamEditDto>, TeamEditDtoValidator>();
        builder.Services.AddScoped<IValidator<TournamentEditDto>, TournamentEditDtoValidator>();
        builder.Services.AddScoped<IValidator<WordDocGenerationDto>, WordDocGenerationDtoValidator>();

        // Database
        builder.Services.AddDbContext<ApplicationDbContext>(
            (serviceProvider, options) =>
            {
                var dataService = serviceProvider.GetRequiredService<UserDataService>();
                options.UseSqlite($"Data Source={dataService.GetDatabasePath()}");
            }
        );

        // Json serialisation
        builder.Services.ConfigureHttpJsonOptions(options =>
        {
            options.SerializerOptions.Converters.Add(new DateOnlyToTimestampConverter());
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
        ApplyDatabaseMigrations(app, userDataService.GetDatabasePath());

        // Development pages
#if DEBUG
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

    private static void ApplyDatabaseMigrations(WebApplication app, string dbPath)
    {
        bool hasPendingMigrations;
        var isInitialCreate = !File.Exists(dbPath);

        using (var checkScope = app.Services.CreateScope())
        {
            var dbContext = checkScope.ServiceProvider.GetRequiredService<ApplicationDbContext>();
            hasPendingMigrations = dbContext.Database.GetPendingMigrations().Any();
        }

        if (!hasPendingMigrations && !isInitialCreate)
        {
            app.Logger.LogInformation("No database migrations needed");
            return;
        }

        // Only backup if already exists
        if (!isInitialCreate)
        {
            app.Logger.LogInformation("Pending migrations detected. Creating backup...");

            if (!File.Exists(dbPath))
            {
                app.Logger.LogCritical("Database file not found at: {DbPath}", dbPath);
                Environment.Exit(-1);
            }

            var backupPath = Path.Combine(
                Path.GetDirectoryName(dbPath)!,
                $"{Path.GetFileNameWithoutExtension(dbPath)}_backup_{DateTime.Now:yyyyMMdd_HHmmss}.db"
            );

            File.Copy(dbPath, backupPath);
            app.Logger.LogInformation("Backup created at: {BackupPath}", backupPath);
        }

        using (var migrateScope = app.Services.CreateScope())
        {
            var dbContext = migrateScope.ServiceProvider.GetRequiredService<ApplicationDbContext>();
            app.Logger.LogInformation("Applying migrations...");
            dbContext.Database.Migrate();
        }
    }
}

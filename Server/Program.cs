using System.Text.Json.Serialization;
using FluentValidation;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Http.Json;
using Microsoft.EntityFrameworkCore;
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
using Turnierverwaltung.Server.Results.Scoreboard;
using Turnierverwaltung.Server.Results.Word;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server;

public static partial class Program
{
    public static void Main(string[] args)
    {
        ValidatorOptions.Global.LanguageManager.Enabled = false;
        var dataDir = GetDataDirectory();

        var builder = WebApplication.CreateBuilder(args);
        builder.Configuration.AddJsonFile(dataDir + "/appsettings.json", optional: true, reloadOnChange: false);

        var services = builder.Services;
        services.AddAppApiDoc();

        // Add validators
        services.AddScoped<IValidator<TournamentEditDto>, TournamentEditDtoValidator>();
        services.AddScoped<IValidator<ClubEditDto>, ClubEditDtoValidator>();
        services.AddScoped<IValidator<DisciplineEditDto>, DisciplineEditDtoValidator>();
        services.AddScoped<IValidator<ParticipantEditDto>, ParticipantEditDtoValidator>();
        services.AddScoped<IValidator<ParticipantResultEditDto>, ParticipantResultEditDtoValidator>();
        services.AddScoped<IValidator<TeamDisciplineEditDto>, TeamDisciplineEditDtoValidator>();
        services.AddScoped<IValidator<WordDocGenerationDto>, WordDocGenerationDtoValidator>();

        services.AddScoped<IScoreboardDataCreator, ScoreboardDataCreator>();
        services.AddScoped<IWordFileCreator, WordFileCreator>();

        // Add database context
        var connectionString = $"Data Source={GetUserData(UserDataType.Database)}";
        services.AddDbContext<ApplicationDbContext>(options =>
        {
            options.UseSqlite(connectionString);
        });

        // Add json converter
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
        app.MapFallbackToFile("index.html");

        app.Run();
    }

    private static string GetDataDirectory()
    {
        var dir = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
        if (dir == null)
            throw new DirectoryNotFoundException("Could not find suitable data dir");

        dir += "/Turnierverwaltung";

        Directory.CreateDirectory(dir);

        return dir;
    }

    public static string GetUserData(UserDataType type)
    {
        var dir = GetDataDirectory();

        dir += type switch
        {
            UserDataType.Database => "/Data.db",
            UserDataType.WordDocumentIcon => "/Custom/WordIcon.png",
            _ => throw new ArgumentOutOfRangeException(nameof(type), type, null),
        };

        return dir;
    }
}

using System.Text.Json.Serialization;
using FluentValidation;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Http.Json;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Auth;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Model.Transfer;
using Turnierverwaltung.Server.Model.Transfer.Club;
using Turnierverwaltung.Server.Model.Transfer.Discipline;
using Turnierverwaltung.Server.Model.Transfer.Participant;
using Turnierverwaltung.Server.Model.Transfer.TeamDiscipline;
using Turnierverwaltung.Server.Model.Transfer.Tournament;
using Turnierverwaltung.Server.Model.Validation;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server;

public static partial class Program
{
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
        var connectionString = $"Data Source={GetUserData(UserDataType.Database)}";
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
}

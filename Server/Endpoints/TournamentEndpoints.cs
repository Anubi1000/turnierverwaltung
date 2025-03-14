using FluentValidation;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using SharpGrip.FluentValidation.AutoValidation.Shared.Extensions;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Model.Transfer.Tournament;
using Turnierverwaltung.Server.Model.Validation;

namespace Turnierverwaltung.Server.Endpoints;

public static class TournamentEndpoints
{
    public static IEndpointRouteBuilder MapTournamentEndpoints(this IEndpointRouteBuilder builder)
    {
        var group = builder.MapGroup("/api/tournaments").WithTags("Tournament").RequireAuthorization();

        group.MapGet("/", GetTournaments);

        group.MapPost("/", CreateTournament);

        group.MapGet("/{tournamentId:int}", GetTournament);

        group.MapPut("/{tournamentId:int}", UpdateTournament);

        group.MapDelete("/{tournamentId:int}", DeleteTournament);

        return builder;
    }

    /// <summary>
    ///     Retrieves a list of tournaments from the database, ordered by date, name, and ID.
    /// </summary>
    /// <param name="dbContext">The application database context used to query tournaments.</param>
    /// <returns>
    ///     An <see cref="IResult" /> containing an HTTP 200 OK response with a list of <see cref="ListTournamentDto" />
    ///     objects.
    /// </returns>
    public static async Task<Ok<List<ListTournamentDto>>> GetTournaments(ApplicationDbContext dbContext)
    {
        var tournaments = await dbContext
            .Tournaments.AsNoTracking()
            .OrderByDescending(t => t.Date)
            .ThenBy(t => t.Name)
            .ThenBy(t => t.Id)
            .Select(t => new ListTournamentDto(t.Id, t.Name, t.Date))
            .ToListAsync();

        return TypedResults.Ok(tournaments);
    }

    /// <summary>
    ///     Creates a new tournament and saves it to the database.
    /// </summary>
    /// <param name="dbContext">The application database context used to add and save the new tournament.</param>
    /// <param name="validator">The validator for <see cref="TournamentEditDto" /> to ensure the input is valid.</param>
    /// <param name="dto">The data transfer object containing the tournament details to be created.</param>
    /// <returns>
    ///     An <see cref="IResult" /> containing:
    ///     - HTTP 200 OK with the created tournament's ID if successful.
    ///     - HTTP 400 Bad Request with validation errors if the input is invalid.
    /// </returns>
    public static async Task<Results<ValidationProblem, Ok<int>>> CreateTournament(
        ApplicationDbContext dbContext,
        IValidator<TournamentEditDto> validator,
        TournamentEditDto dto
    )
    {
        // Validate dto and return ValidationProblem if not valid
        var validationResult = await validator.ValidateAsync(dto);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        // Sanitize dto
        dto = SanitizeEditDto(dto);

        // Create a tournament from the dto
        var tournament = new Tournament
        {
            Name = dto.Name,
            Date = dto.Date,
            TeamSize = dto.TeamSize,
        };

        // Save tournament to database
        dbContext.Add(tournament);
        await dbContext.SaveChangesAsync();

        return TypedResults.Ok(tournament.Id);
    }

    /// <summary>
    ///     Retrieves the details of a specific tournament by its ID.
    /// </summary>
    /// <param name="dbContext">The application database context used to query the tournament.</param>
    /// <param name="tournamentId">The ID of the tournament to retrieve.</param>
    /// <returns>
    ///     An <see cref="IResult" /> containing:
    ///     - HTTP 200 OK with the tournament details if found.
    ///     - HTTP 404 Not Found if no tournament with the given ID exists.
    /// </returns>
    public static async Task<Results<NotFound, Ok<TournamentDetailDto>>> GetTournament(
        ApplicationDbContext dbContext,
        int tournamentId
    )
    {
        var tournament = await dbContext
            .Tournaments.AsNoTracking()
            .Where(t => t.Id == tournamentId)
            .Select(t => new TournamentDetailDto(
                t.Id,
                t.Name,
                t.Date,
                t.TeamSize,
                t.Clubs.Count,
                t.Disciplines.Count + t.TeamDisciplines.Count,
                t.Participants.Count,
                t.Teams.Count
            ))
            .SingleOrDefaultAsync();

        return tournament is null ? TypedResults.NotFound() : TypedResults.Ok(tournament);
    }

    /// <summary>
    ///     Updates an existing tournament with new details provided in the DTO.
    /// </summary>
    /// <param name="dbContext">The application database context used to query and update the tournament.</param>
    /// <param name="validator">The validator for <see cref="TournamentEditDto" /> to ensure the input is valid.</param>
    /// <param name="tournamentId">The ID of the tournament to be updated.</param>
    /// <param name="dto">The data transfer object containing the updated tournament details.</param>
    /// <returns>
    ///     An <see cref="IResult" /> containing:
    ///     - HTTP 200 OK if the tournament is successfully updated.
    ///     - HTTP 404 Not Found if no tournament with the given ID exists.
    ///     - HTTP 400 Bad Request with validation errors if the input is invalid.
    /// </returns>
    public static async Task<Results<NotFound, ValidationProblem, Ok>> UpdateTournament(
        ApplicationDbContext dbContext,
        IValidator<TournamentEditDto> validator,
        int tournamentId,
        TournamentEditDto dto
    )
    {
        // Query tournament and return NotFound if it does not exist
        var tournament = await dbContext.Tournaments.FindAsync(tournamentId);
        if (tournament is null)
            return TypedResults.NotFound();

        // Validate dto and return ValidationProblem if not valid
        var context = new ValidationContext<TournamentEditDto>(dto)
        {
            RootContextData = { [TournamentEditDtoValidator.PreviousTeamSizeKey] = tournament.TeamSize },
        };

        var validationResult = await validator.ValidateAsync(context);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        // Sanitize dto
        dto = SanitizeEditDto(dto);

        // Update properties of tournament and save changes
        tournament.Name = dto.Name;
        tournament.Date = dto.Date;

        await dbContext.SaveChangesAsync();

        return TypedResults.Ok();
    }

    /// <summary>
    ///     Deletes a tournament by its ID from the database.
    /// </summary>
    /// <param name="dbContext">The application database context used to query and remove the tournament.</param>
    /// <param name="tournamentId">The ID of the tournament to be deleted.</param>
    /// <returns>
    ///     An <see cref="IResult" /> containing:
    ///     - HTTP 200 OK if the tournament is successfully deleted.
    ///     - HTTP 404 Not Found if no tournament with the given ID exists.
    /// </returns>
    public static async Task<Results<NotFound, Ok>> DeleteTournament(ApplicationDbContext dbContext, int tournamentId)
    {
        var tournament = await dbContext.Tournaments.FindAsync(tournamentId);
        if (tournament is null)
            return TypedResults.NotFound();

        dbContext.Remove(tournament);
        await dbContext.SaveChangesAsync();

        return TypedResults.Ok();
    }

    private static TournamentEditDto SanitizeEditDto(TournamentEditDto dto)
    {
        return dto with { Name = dto.Name.Trim() };
    }
}

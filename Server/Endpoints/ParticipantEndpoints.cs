using FluentValidation;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using SharpGrip.FluentValidation.AutoValidation.Shared.Extensions;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Model.Transfer.Participant;
using Turnierverwaltung.Server.Model.Validation;

namespace Turnierverwaltung.Server.Endpoints;

public static class ParticipantEndpoints
{
    public static IEndpointRouteBuilder MapParticipantEndpoints(this IEndpointRouteBuilder builder)
    {
        var baseGroup = builder.MapGroup("/api").WithTags("Participant").RequireAuthorization();

        var tournamentDependentGroup = baseGroup.MapGroup("/tournaments/{tournamentId:int}/participants");
        var tournamentIndependentGroup = baseGroup.MapGroup("/participants/{participantId:int}");

        // Tournament-based routes
        tournamentDependentGroup.MapGet("/", GetParticipants);

        tournamentDependentGroup.MapPost("/", CreateParticipant);

        // Participant routes
        tournamentIndependentGroup.MapGet("/", GetParticipant);

        tournamentIndependentGroup.MapPut("/", UpdateParticipant);

        tournamentIndependentGroup.MapDelete("/", DeleteParticipant);

        // Result routes
        tournamentIndependentGroup.MapGet("/results/{disciplineId:int}", GetParticipantResults);

        tournamentIndependentGroup.MapPut("/results/{disciplineId:int}", UpdateParticipantResults);

        return builder;
    }

    public static async Task<Results<NotFound, Ok<List<ListParticipantDto>>>> GetParticipants(
        ApplicationDbContext dbContext,
        int tournamentId
    )
    {
        if (!await dbContext.Tournaments.AsNoTracking().AnyAsync(t => t.Id == tournamentId))
            return TypedResults.NotFound();

        var participants = await dbContext
            .Participants.AsNoTracking()
            .Where(p => p.TournamentId == tournamentId)
            .OrderBy(p => p.StartNumber)
            .ThenBy(p => p.Name)
            .ThenBy(p => p.Id)
            .Select(p => new ListParticipantDto(p.Id, p.Name, p.StartNumber))
            .ToListAsync();

        return TypedResults.Ok(participants);
    }

    private static async Task<Results<NotFound, ValidationProblem, Ok<int>>> CreateParticipant(
        ApplicationDbContext dbContext,
        IValidator<ParticipantEditDto> validator,
        int tournamentId,
        ParticipantEditDto dto
    )
    {
        if (!await dbContext.Tournaments.AsNoTracking().AnyAsync(t => t.Id == tournamentId))
            return TypedResults.NotFound();

        var context = new ValidationContext<ParticipantEditDto>(dto)
        {
            RootContextData = { [ParticipantEditDtoValidator.TournamentIdKey] = tournamentId },
        };

        var validationResult = await validator.ValidateAsync(context);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        dto = SanitizeEditDto(dto);

        var participant = new Participant
        {
            Name = dto.Name,
            StartNumber = dto.StartNumber,
            Gender = dto.Gender,
            ClubId = dto.ClubId,
            TournamentId = tournamentId,
        };

        dbContext.Participants.Add(participant);
        await dbContext.SaveChangesAsync();

        return TypedResults.Ok(participant.Id);
    }

    private static async Task<Results<NotFound, Ok<ParticipantDetailDto>>> GetParticipant(
        ApplicationDbContext dbContext,
        int participantId
    )
    {
        var participant = await dbContext
            .Participants.AsNoTracking()
            .Where(p => p.Id == participantId)
            .Select(p => new ParticipantDetailDto(
                p.Id,
                p.Name,
                p.StartNumber,
                p.Gender,
                p.ClubId,
                p.Club.Name,
                p.Tournament.Disciplines.OrderBy(d => d.Name)
                    .Select(d => new ParticipantDetailDto.Discipline(d.Id, d.Name))
                    .ToList()
            ))
            .SingleOrDefaultAsync();

        return participant is null ? TypedResults.NotFound() : TypedResults.Ok(participant);
    }

    private static async Task<Results<NotFound, ValidationProblem, Ok>> UpdateParticipant(
        ApplicationDbContext dbContext,
        IValidator<ParticipantEditDto> validator,
        int participantId,
        ParticipantEditDto dto
    )
    {
        var participant = await dbContext.Participants.FindAsync(participantId);
        if (participant is null)
            return TypedResults.NotFound();

        var context = new ValidationContext<ParticipantEditDto>(dto)
        {
            RootContextData = { [ParticipantEditDtoValidator.TournamentIdKey] = participant.TournamentId },
        };

        var validationResult = await validator.ValidateAsync(context);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        dto = SanitizeEditDto(dto);

        participant.Name = dto.Name;
        participant.StartNumber = dto.StartNumber;
        participant.Gender = dto.Gender;
        participant.ClubId = dto.ClubId;

        await dbContext.SaveChangesAsync();

        return TypedResults.Ok();
    }

    private static async Task<Results<NotFound, Ok>> DeleteParticipant(
        ApplicationDbContext dbContext,
        int participantId
    )
    {
        var participant = await dbContext.Participants.FindAsync(participantId);
        if (participant is null)
            return TypedResults.NotFound();

        dbContext.Remove(participant);
        await dbContext.SaveChangesAsync();

        return TypedResults.Ok();
    }

    private static async Task<Results<NotFound, Ok<ParticipantResultDetailDto>>> GetParticipantResults(
        ApplicationDbContext dbContext,
        int participantId,
        int disciplineId
    )
    {
        if (!await dbContext.Participants.AsNoTracking().AnyAsync(p => p.Id == participantId))
            return TypedResults.NotFound();

        var discipline = await dbContext.Disciplines.AsNoTracking().FirstOrDefaultAsync(d => d.Id == disciplineId);
        if (discipline == null)
            return TypedResults.NotFound();

        var disciplineValues = discipline
            .Values.Select(v => new ParticipantResultDetailDto.DisciplineValue(v.Name, v.IsAdded))
            .ToList();

        var participantResult = await dbContext
            .ParticipantResults.AsNoTracking()
            .FirstOrDefaultAsync(pr => pr.ParticipantId == participantId && pr.DisciplineId == disciplineId);

        if (participantResult is null)
            return TypedResults.Ok(new ParticipantResultDetailDto(disciplineValues, []));

        var roundResults = participantResult
            .Rounds.Select(round => new ParticipantResultDetailDto.RoundResult(round.Values))
            .ToList();

        var resultDto = new ParticipantResultDetailDto(disciplineValues, roundResults);
        return TypedResults.Ok(resultDto);
    }

    /// <summary>
    /// Updates the results of a participant for a given discipline.
    /// </summary>
    /// <param name="dbContext">The database context used to access participant and discipline data.</param>
    /// <param name="validator">The validator for validating the participant result data.</param>
    /// <param name="participantId">The ID of the participant whose results are being updated.</param>
    /// <param name="disciplineId">The ID of the discipline associated with the participant's Typedresults.</param>
    /// <param name="dto">The data transfer object containing the updated participant Typedresults.</param>
    /// <returns>A result indicating the outcome of the update operation.</returns>
    private static async Task<Results<NotFound, ValidationProblem, Ok>> UpdateParticipantResults(
        ApplicationDbContext dbContext,
        IValidator<ParticipantResultEditDto> validator,
        int participantId,
        int disciplineId,
        ParticipantResultEditDto dto
    )
    {
        // Check if the participant exists
        if (!await dbContext.Participants.AsNoTracking().AnyAsync(p => p.Id == participantId))
            return TypedResults.NotFound();

        // Get the count of discipline values
        var disciplineValueCount = await dbContext
            .Disciplines.AsNoTracking()
            .Where(d => d.Id == disciplineId)
            .Select(d => d.Values.Count)
            .FirstOrDefaultAsync();

        // Return NotFound if no discipline was found
        if (disciplineValueCount is 0)
            return TypedResults.NotFound();

        // Set up validation context with discipline value count and validate dto
        var context = new ValidationContext<ParticipantResultEditDto>(dto)
        {
            RootContextData = { [ParticipantResultEditDtoValidator.DisciplineValueCountKey] = disciplineValueCount },
        };

        var validationResult = await validator.ValidateAsync(context);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        // Retrieve existing participant result
        var participantResult = await dbContext.ParticipantResults.FirstOrDefaultAsync(pr =>
            pr.ParticipantId == participantId && pr.DisciplineId == disciplineId
        );

        // If no rounds are provided, remove existing result if present
        if (dto.Rounds.Count == 0)
        {
            if (participantResult is not null)
                dbContext.Remove(participantResult);
        }
        else
        {
            var rounds = dto.Rounds.Select(x => new ParticipantResult.Round { Values = x.Values }).ToList();

            // Add new result if none exists, otherwise update existing result
            if (participantResult == null)
            {
                participantResult = new ParticipantResult
                {
                    ParticipantId = participantId,
                    DisciplineId = disciplineId,
                    Rounds = rounds,
                };
                dbContext.Add(participantResult);
            }
            else
            {
                participantResult.Rounds = rounds;
            }
        }

        // Save changes to database
        await dbContext.SaveChangesAsync();
        return TypedResults.Ok();
    }

    private static ParticipantEditDto SanitizeEditDto(ParticipantEditDto dto)
    {
        return dto with { Name = dto.Name.Trim() };
    }
}

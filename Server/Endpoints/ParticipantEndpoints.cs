using FluentValidation;
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
        tournamentDependentGroup
            .MapGet("/", GetParticipants)
            .Produces<List<ListParticipantDto>>()
            .Produces(StatusCodes.Status404NotFound);

        tournamentDependentGroup
            .MapPost("/", CreateParticipant)
            .Produces<int>()
            .Produces(StatusCodes.Status404NotFound);

        // Participant routes
        tournamentIndependentGroup
            .MapGet("/", GetParticipant)
            .Produces<ParticipantDetailDto>()
            .Produces(StatusCodes.Status404NotFound);

        tournamentIndependentGroup
            .MapPut("/", UpdateParticipant)
            .Produces(StatusCodes.Status200OK)
            .Produces(StatusCodes.Status404NotFound);

        tournamentIndependentGroup
            .MapDelete("/", DeleteParticipant)
            .Produces(StatusCodes.Status200OK)
            .Produces(StatusCodes.Status404NotFound);

        // Result routes
        tournamentIndependentGroup
            .MapGet("/results/{disciplineId:int}", GetParticipantResults)
            .Produces<ParticipantResultDetailDto>()
            .Produces(StatusCodes.Status404NotFound);

        tournamentIndependentGroup
            .MapPut("/results/{disciplineId:int}", UpdateParticipantResults)
            .Produces(StatusCodes.Status200OK)
            .Produces(StatusCodes.Status404NotFound);

        return builder;
    }

    public static async Task<IResult> GetParticipants(ApplicationDbContext dbContext, int tournamentId)
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return Results.NotFound();

        var participants = await dbContext
            .Participants.AsNoTracking()
            .Where(p => p.TournamentId == tournamentId)
            .OrderBy(p => p.StartNumber)
            .ThenBy(p => p.Name)
            .ThenBy(p => p.Id)
            .Select(p => new ListParticipantDto(p.Id, p.Name, p.StartNumber))
            .ToListAsync();

        return Results.Ok(participants);
    }

    private static async Task<IResult> CreateParticipant(
        ApplicationDbContext dbContext,
        IValidator<ParticipantEditDto> validator,
        int tournamentId,
        ParticipantEditDto dto
    )
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return Results.NotFound();

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

        return Results.Ok(participant.Id);
    }

    private static async Task<IResult> GetParticipant(ApplicationDbContext dbContext, int participantId)
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

        return participant is null ? Results.NotFound() : Results.Ok(participant);
    }

    private static async Task<IResult> UpdateParticipant(
        ApplicationDbContext dbContext,
        IValidator<ParticipantEditDto> validator,
        int participantId,
        ParticipantEditDto dto
    )
    {
        var participant = await dbContext.Participants.FindAsync(participantId);
        if (participant is null)
            return Results.NotFound();

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

        return Results.Ok();
    }

    private static async Task<IResult> DeleteParticipant(ApplicationDbContext dbContext, int participantId)
    {
        var participant = await dbContext.Participants.FindAsync(participantId);
        if (participant is null)
            return Results.NotFound();

        dbContext.Remove(participant);
        await dbContext.SaveChangesAsync();

        return Results.Ok();
    }

    private static async Task<IResult> GetParticipantResults(
        ApplicationDbContext dbContext,
        int participantId,
        int disciplineId
    )
    {
        var participant = await dbContext.Participants.AsNoTracking().FirstOrDefaultAsync(p => p.Id == participantId);
        if (participant == null)
            return Results.NotFound();

        var discipline = await dbContext.Disciplines.AsNoTracking().FirstOrDefaultAsync(d => d.Id == disciplineId);
        if (discipline == null)
            return Results.NotFound();

        var disciplineValues = discipline
            .Values.Select(v => new ParticipantResultDetailDto.DisciplineValue(v.Name, v.IsAdded))
            .ToList();

        if (!participant.Results.TryGetValue(disciplineId, out var disciplineResult))
        {
            return Results.Ok(new ParticipantResultDetailDto(disciplineValues, []));
        }

        var roundResults = disciplineResult
            .Rounds.Select(r => new ParticipantResultDetailDto.RoundResult(r.Values))
            .ToList();

        var resultDto = new ParticipantResultDetailDto(disciplineValues, roundResults);
        return Results.Ok(resultDto);
    }

    private static async Task<IResult> UpdateParticipantResults(
        ApplicationDbContext dbContext,
        IValidator<ParticipantResultEditDto> validator,
        int participantId,
        int disciplineId,
        ParticipantResultEditDto dto
    )
    {
        var participant = await dbContext.Participants.FindAsync(participantId);
        if (participant == null)
            return Results.NotFound();

        var discipline = await dbContext.Disciplines.AsNoTracking().FirstOrDefaultAsync(d => d.Id == disciplineId);
        if (discipline == null)
            return Results.NotFound();

        var context = new ValidationContext<ParticipantResultEditDto>(dto)
        {
            RootContextData = { [ParticipantResultEditDtoValidator.DisciplineValueCountKey] = discipline.Values.Count },
        };

        var validationResult = await validator.ValidateAsync(context);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        if (dto.Rounds.Count == 0)
        {
            participant.Results.Remove(disciplineId);
        }
        else
        {
            var rounds = dto.Rounds.Select(x => new Participant.RoundResult { Values = x.Values }).ToList();

            participant.Results[disciplineId] = new Participant.DisciplineResult { Rounds = rounds };
        }

        await dbContext.SaveChangesAsync();
        return Results.Ok();
    }

    private static ParticipantEditDto SanitizeEditDto(ParticipantEditDto dto)
    {
        return dto with { Name = dto.Name.Trim() };
    }
}

using FluentValidation;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SharpGrip.FluentValidation.AutoValidation.Shared.Extensions;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Model.Transfer.Participant;
using Turnierverwaltung.Server.Model.Validation;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Endpoints;

public static class ParticipantEndpoints
{
    public static IEndpointRouteBuilder MapParticipantEndpoints(this IEndpointRouteBuilder builder)
    {
        var baseGroup = builder.MapGroup("/api").WithTags("Participant").RequireAuthorization();

        var tournamentDependentGroup = baseGroup.MapGroup("/tournaments/{tournamentId:int}/participants");
        var tournamentIndependentGroup = baseGroup.MapGroup("/participants/{participantId:int}");

        // Tournament-based routes
        tournamentDependentGroup.MapGet("/", GetParticipants).WithName("GetParticipants");

        tournamentDependentGroup.MapPost("/", CreateParticipant).WithName("CreateParticipant");

        tournamentDependentGroup
            .MapGet("/nextStartNumber", GetNextParticipantStartNumber)
            .WithName("GetNextParticipantStartNumber");

        // Participant routes
        tournamentIndependentGroup.MapGet("/", GetParticipant).WithName("GetParticipant");

        tournamentIndependentGroup.MapPut("/", UpdateParticipant).WithName("UpdateParticipant");

        tournamentIndependentGroup.MapDelete("/", DeleteParticipant).WithName("DeleteParticipant");

        return builder;
    }

    public static async Task<Results<NotFound, Ok<List<ListParticipantDto>>>> GetParticipants(
        [FromServices] ApplicationDbContext dbContext,
        [FromRoute] int tournamentId
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

    private static async Task<Results<NotFound, Ok<int>>> GetNextParticipantStartNumber(
        [FromServices] ApplicationDbContext dbContext,
        [FromRoute] int tournamentId
    )
    {
        if (!await dbContext.Tournaments.AsNoTracking().AnyAsync(t => t.Id == tournamentId))
            return TypedResults.NotFound();

        var currentMaxStartNumber = await dbContext
            .Participants.AsNoTracking()
            .Where(p => p.TournamentId == tournamentId)
            .MaxAsync(p => (int?)p.StartNumber);

        return TypedResults.Ok(currentMaxStartNumber.HasValue ? currentMaxStartNumber.Value + 1 : 1);
    }

    public static async Task<Results<NotFound, ValidationProblem, Ok<int>>> CreateParticipant(
        [FromServices] ApplicationDbContext dbContext,
        [FromServices] IValidator<ParticipantEditDto> validator,
        [FromServices] IScoreboardManager scoreboardManager,
        [FromRoute] int tournamentId,
        [FromBody] ParticipantEditDto dto
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

        scoreboardManager.NotifyUpdate(tournamentId);

        return TypedResults.Ok(participant.Id);
    }

    public static async Task<Results<NotFound, Ok<ParticipantDetailDto>>> GetParticipant(
        [FromServices] ApplicationDbContext dbContext,
        [FromRoute] int participantId
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

    public static async Task<Results<NotFound, ValidationProblem, Ok>> UpdateParticipant(
        [FromServices] ApplicationDbContext dbContext,
        [FromServices] IValidator<ParticipantEditDto> validator,
        [FromServices] IScoreboardManager scoreboardManager,
        [FromRoute] int participantId,
        [FromBody] ParticipantEditDto dto
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
        scoreboardManager.NotifyUpdate(participant.TournamentId);

        return TypedResults.Ok();
    }

    public static async Task<Results<NotFound, Ok>> DeleteParticipant(
        [FromServices] ApplicationDbContext dbContext,
        [FromServices] IScoreboardManager scoreboardManager,
        [FromRoute] int participantId
    )
    {
        var participant = await dbContext.Participants.FindAsync(participantId);
        if (participant is null)
            return TypedResults.NotFound();

        dbContext.Remove(participant);
        await dbContext.SaveChangesAsync();

        scoreboardManager.NotifyUpdate(participant.TournamentId);

        return TypedResults.Ok();
    }

    private static ParticipantEditDto SanitizeEditDto(ParticipantEditDto dto)
    {
        return dto with { Name = dto.Name.Trim() };
    }
}

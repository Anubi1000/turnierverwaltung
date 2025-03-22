using FluentValidation;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using SharpGrip.FluentValidation.AutoValidation.Shared.Extensions;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Model.Transfer.Participant;
using Turnierverwaltung.Server.Model.Validation;

namespace Turnierverwaltung.Server.Endpoints;

public static class ParticipantResultEndpoints
{
    public static IEndpointRouteBuilder MapParticipantResultEndpoints(this IEndpointRouteBuilder builder)
    {
        var baseGroup = builder.MapGroup("/api/participants/{participantId:int}/results/{disciplineId:int}").WithTags("Participant / Results").RequireAuthorization();

        baseGroup.MapGet(
            "/",
            GetParticipantResults
        );

        baseGroup.MapPut(
            "/",
            UpdateParticipantResults
        );

        return builder;
    }

    public static async Task<Results<NotFound, Ok<ParticipantResultDetailDto>>> GetParticipantResults(
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
    ///     Updates the results of a participant for a given discipline.
    /// </summary>
    /// <param name="dbContext">The database context used to access participant and discipline data.</param>
    /// <param name="validator">The validator for validating the participant result data.</param>
    /// <param name="participantId">The ID of the participant whose results are being updated.</param>
    /// <param name="disciplineId">The ID of the discipline associated with the participant's Typedresults.</param>
    /// <param name="dto">The data transfer object containing the updated participant Typedresults.</param>
    /// <returns>A result indicating the outcome of the update operation.</returns>
    public static async Task<Results<NotFound, ValidationProblem, Ok>> UpdateParticipantResults(
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
}

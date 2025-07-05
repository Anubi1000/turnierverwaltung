using FluentValidation;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SharpGrip.FluentValidation.AutoValidation.Shared.Extensions;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Model.Transfer.Discipline;
using Turnierverwaltung.Server.Model.Validation;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Endpoints;

public static class DisciplineEndpoints
{
    public static IEndpointRouteBuilder MapDisciplineEndpoints(this IEndpointRouteBuilder builder)
    {
        var baseGroup = builder.MapGroup("/api").WithTags("Discipline").RequireAuthorization();

        var tournamentDependentGroup = baseGroup.MapGroup("/tournaments/{tournamentId:int}/disciplines");
        var tournamentIndependentGroup = baseGroup.MapGroup("/disciplines/{disciplineId:int}");

        // Tournament-based routes
        tournamentDependentGroup.MapGet("/", GetDisciplines).WithName("GetDisciplines");

        tournamentDependentGroup.MapPost("/", CreateDiscipline).WithName("CreateDiscipline");

        // Discipline routes
        tournamentIndependentGroup.MapGet("/", GetDiscipline).WithName("GetDiscipline");

        tournamentIndependentGroup.MapPut("/", UpdateDiscipline).WithName("UpdateDiscipline");

        tournamentIndependentGroup.MapDelete("/", DeleteDiscipline).WithName("DeleteDiscipline");

        return builder;
    }

    public static async Task<Results<NotFound, Ok<List<ListDisciplineDto>>>> GetDisciplines(
        [FromServices] ApplicationDbContext dbContext,
        [FromRoute] int tournamentId
    )
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return TypedResults.NotFound();

        var disciplines = await dbContext
            .Disciplines.AsNoTracking()
            .Where(d => d.TournamentId == tournamentId)
            .OrderBy(d => d.Name)
            .ThenBy(d => d.Id)
            .Select(d => new ListDisciplineDto(d.Id, d.Name))
            .ToListAsync();

        return TypedResults.Ok(disciplines);
    }

    public static async Task<Results<NotFound, ValidationProblem, Ok<int>>> CreateDiscipline(
        [FromServices] ApplicationDbContext dbContext,
        [FromServices] IValidator<DisciplineEditDto> validator,
        [FromServices] IScoreboardManager scoreboardManager,
        [FromRoute] int tournamentId,
        [FromBody] DisciplineEditDto dto
    )
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return TypedResults.NotFound();

        var validationResult = await validator.ValidateAsync(dto);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        dto = SanitizeEditDto(dto);

        var discipline = new Discipline
        {
            Name = dto.Name,
            AmountOfBestRoundsToShow = dto.AmountOfBestRoundsToShow,
            AreGendersSeparated = dto.AreGendersSeparated,
            ShowInResults = dto.ShowInResults,
            TournamentId = tournamentId,
            Values = dto
                .Values.Select(value => new Discipline.Value { Name = value.Name, IsAdded = value.IsAdded })
                .ToList(),
        };

        dbContext.Add(discipline);
        await dbContext.SaveChangesAsync();

        scoreboardManager.NotifyUpdate(tournamentId);

        return TypedResults.Ok(discipline.Id);
    }

    public static async Task<Results<NotFound, Ok<DisciplineDetailDto>>> GetDiscipline(
        [FromServices] ApplicationDbContext dbContext,
        [FromRoute] int disciplineId
    )
    {
        var discipline = await dbContext
            .Disciplines.AsNoTracking()
            .Where(d => d.Id == disciplineId)
            .Select(d => new
            {
                d.Id,
                d.TournamentId,
                d.Name,
                d.AmountOfBestRoundsToShow,
                d.AreGendersSeparated,
                d.ShowInResults,
                d.Values,
            })
            .FirstOrDefaultAsync();

        if (discipline is null)
            return TypedResults.NotFound();

        var result = new DisciplineDetailDto(
            discipline.Id,
            discipline.TournamentId,
            discipline.Name,
            discipline.AmountOfBestRoundsToShow,
            discipline.AreGendersSeparated,
            discipline.ShowInResults,
            discipline.Values.Select(v => new DisciplineDetailDto.Value(v.Name, v.IsAdded)).ToList()
        );

        return TypedResults.Ok(result);
    }

    public static async Task<Results<NotFound, ValidationProblem, Ok>> UpdateDiscipline(
        [FromServices] ApplicationDbContext dbContext,
        [FromServices] IValidator<DisciplineEditDto> validator,
        [FromServices] IScoreboardManager scoreboardManager,
        [FromRoute] int disciplineId,
        [FromBody] DisciplineEditDto dto
    )
    {
        var discipline = await dbContext.Disciplines.FindAsync(disciplineId);
        if (discipline is null)
            return TypedResults.NotFound();

        var context = new ValidationContext<DisciplineEditDto>(dto)
        {
            RootContextData = { [DisciplineEditDtoValidator.PreviousValueAmountKey] = discipline.Values.Count },
        };

        var validationResult = await validator.ValidateAsync(context);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        dto = SanitizeEditDto(dto);

        discipline.Name = dto.Name;
        discipline.AmountOfBestRoundsToShow = dto.AmountOfBestRoundsToShow;
        discipline.AreGendersSeparated = dto.AreGendersSeparated;
        discipline.ShowInResults = dto.ShowInResults;

        discipline.Values.Clear();
        foreach (var value in dto.Values.Select(v => new Discipline.Value { Name = v.Name, IsAdded = v.IsAdded }))
            discipline.Values.Add(value);

        await dbContext.SaveChangesAsync();
        scoreboardManager.NotifyUpdate(discipline.TournamentId);

        return TypedResults.Ok();
    }

    public static async Task<Results<NotFound, Ok>> DeleteDiscipline(
        [FromServices] ApplicationDbContext dbContext,
        [FromServices] IScoreboardManager scoreboardManager,
        [FromRoute] int disciplineId
    )
    {
        var discipline = await dbContext.Disciplines.FindAsync(disciplineId);
        if (discipline is null)
            return TypedResults.NotFound();

        dbContext.Remove(discipline);
        await dbContext.SaveChangesAsync();

        scoreboardManager.NotifyUpdate(discipline.TournamentId);

        return TypedResults.Ok();
    }

    private static DisciplineEditDto SanitizeEditDto(DisciplineEditDto dto)
    {
        var values = dto.Values.Select(value => value with { Name = value.Name.Trim() }).ToList();
        return dto with { Name = dto.Name.Trim(), Values = values };
    }
}

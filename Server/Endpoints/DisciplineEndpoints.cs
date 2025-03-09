using FluentValidation;
using Microsoft.EntityFrameworkCore;
using SharpGrip.FluentValidation.AutoValidation.Shared.Extensions;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Model.Transfer.Discipline;
using Turnierverwaltung.Server.Model.Validation;

namespace Turnierverwaltung.Server.Endpoints;

public static class DisciplineEndpoints
{
    public static IEndpointRouteBuilder MapDisciplineEndpoints(this IEndpointRouteBuilder builder)
    {
        var baseGroup = builder.MapGroup("/api").WithTags("Discipline").RequireAuthorization();

        var tournamentDependentGroup = baseGroup.MapGroup("/tournaments/{tournamentId:int}/disciplines");
        var tournamentIndependentGroup = baseGroup.MapGroup("/disciplines/{disciplineId:int}");

        // Tournament-based routes
        tournamentDependentGroup
            .MapGet("/", GetDisciplines)
            .Produces<List<ListDisciplineDto>>()
            .Produces(StatusCodes.Status404NotFound);

        tournamentDependentGroup.MapPost("/", CreateDiscipline).Produces<int>().Produces(StatusCodes.Status404NotFound);

        // Discipline routes
        tournamentIndependentGroup
            .MapGet("/", GetDiscipline)
            .Produces<DisciplineDetailDto>()
            .Produces(StatusCodes.Status404NotFound);

        tournamentIndependentGroup
            .MapPut("/", UpdateDiscipline)
            .Produces(StatusCodes.Status200OK)
            .Produces(StatusCodes.Status404NotFound);

        tournamentIndependentGroup
            .MapDelete("/", DeleteDiscipline)
            .Produces(StatusCodes.Status200OK)
            .Produces(StatusCodes.Status404NotFound);

        return builder;
    }

    public static async Task<IResult> GetDisciplines(ApplicationDbContext dbContext, int tournamentId)
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return Results.NotFound();

        var disciplines = await dbContext
            .Disciplines.AsNoTracking()
            .Where(d => d.TournamentId == tournamentId)
            .OrderBy(d => d.Name)
            .ThenBy(d => d.Id)
            .Select(d => new ListDisciplineDto(d.Id, d.Name))
            .ToListAsync();

        return Results.Ok(disciplines);
    }

    public static async Task<IResult> CreateDiscipline(
        ApplicationDbContext dbContext,
        IValidator<DisciplineEditDto> validator,
        int tournamentId,
        DisciplineEditDto dto
    )
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return Results.NotFound();

        var validationResult = await validator.ValidateAsync(dto);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        dto = SanitizeEditDto(dto);

        var discipline = new Discipline
        {
            Name = dto.Name,
            AmountOfBestRoundsToShow = dto.AmountOfBestRoundsToShow,
            AreGendersSeparated = dto.AreGendersSeparated,
            TournamentId = tournamentId,
            Values = dto
                .Values.Select(value => new Discipline.Value { Name = value.Name, IsAdded = value.IsAdded })
                .ToList(),
        };

        dbContext.Add(discipline);
        await dbContext.SaveChangesAsync();

        return Results.Ok(discipline.Id);
    }

    public static async Task<IResult> GetDiscipline(ApplicationDbContext dbContext, int disciplineId)
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
                d.Values,
            })
            .FirstOrDefaultAsync();

        if (discipline is null)
            return Results.NotFound();

        var result = new DisciplineDetailDto(
            discipline.Id,
            discipline.TournamentId,
            discipline.Name,
            discipline.AmountOfBestRoundsToShow,
            discipline.AreGendersSeparated,
            discipline.Values.Select(v => new DisciplineDetailDto.Value(v.Name, v.IsAdded)).ToList()
        );

        return Results.Ok(result);
    }

    public static async Task<IResult> UpdateDiscipline(
        ApplicationDbContext dbContext,
        IValidator<DisciplineEditDto> validator,
        int disciplineId,
        DisciplineEditDto dto
    )
    {
        var discipline = await dbContext.Disciplines.FindAsync(disciplineId);
        if (discipline is null)
            return Results.NotFound();

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

        discipline.Values.Clear();
        foreach (var value in dto.Values.Select(v => new Discipline.Value { Name = v.Name, IsAdded = v.IsAdded }))
            discipline.Values.Add(value);

        await dbContext.SaveChangesAsync();

        return Results.Ok();
    }

    public static async Task<IResult> DeleteDiscipline(ApplicationDbContext dbContext, int disciplineId)
    {
        var discipline = await dbContext.Disciplines.FindAsync(disciplineId);
        if (discipline is null)
            return Results.NotFound();

        dbContext.Remove(discipline);
        await dbContext.SaveChangesAsync();

        return Results.Ok();
    }

    private static DisciplineEditDto SanitizeEditDto(DisciplineEditDto dto)
    {
        var values = dto.Values.Select(value => value with { Name = value.Name.Trim() }).ToList();
        return dto with { Name = dto.Name.Trim(), Values = values };
    }
}

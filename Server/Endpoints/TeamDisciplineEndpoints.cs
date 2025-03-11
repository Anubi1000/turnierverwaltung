using FluentValidation;
using Microsoft.EntityFrameworkCore;
using SharpGrip.FluentValidation.AutoValidation.Shared.Extensions;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Model.Transfer.TeamDiscipline;
using Turnierverwaltung.Server.Model.Validation;

namespace Turnierverwaltung.Server.Endpoints;

public static class TeamDisciplineEndpoints
{
    public static IEndpointRouteBuilder MapTeamDisciplineEndpoints(this IEndpointRouteBuilder builder)
    {
        var baseGroup = builder.MapGroup("/api").WithTags("TeamDiscipline").RequireAuthorization();

        var tournamentDependentGroup = baseGroup.MapGroup("/tournaments/{tournamentId:int}/team_disciplines");
        var tournamentIndependentGroup = baseGroup.MapGroup("/team_disciplines/{teamDisciplineId:int}");

        tournamentDependentGroup
            .MapGet("/", GetTeamDisciplines)
            .Produces<List<ListTeamDisciplineDto>>()
            .Produces(StatusCodes.Status404NotFound);

        tournamentDependentGroup
            .MapPost("/", CreateTeamDiscipline)
            .Produces<int>()
            .Produces(StatusCodes.Status404NotFound);

        tournamentIndependentGroup
            .MapGet("/", GetTeamDiscipline)
            .Produces<TeamDisciplineDetailDto>()
            .Produces(StatusCodes.Status404NotFound);

        tournamentIndependentGroup
            .MapPut("/", UpdateTeamDiscipline)
            .Produces(StatusCodes.Status200OK)
            .Produces(StatusCodes.Status404NotFound);

        tournamentIndependentGroup
            .MapDelete("/", DeleteTeamDiscipline)
            .Produces(StatusCodes.Status200OK)
            .Produces(StatusCodes.Status404NotFound);
        return builder;
    }

    private static async Task<IResult> GetTeamDisciplines(ApplicationDbContext dbContext, int tournamentId)
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return Results.NotFound();

        var teamDisciplines = await dbContext
            .TeamDisciplines.Where(t => t.TournamentId == tournamentId)
            .OrderBy(t => t.Name)
            .ThenBy(t => t.Id)
            .Select(t => new ListTeamDisciplineDto(t.Id, t.Name))
            .ToListAsync();

        return Results.Ok(teamDisciplines);
    }

    private static async Task<IResult> CreateTeamDiscipline(
        ApplicationDbContext dbContext,
        IValidator<TeamDisciplineEditDto> validator,
        int tournamentId,
        TeamDisciplineEditDto dto
    )
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return Results.NotFound();

        var context = new ValidationContext<TeamDisciplineEditDto>(dto)
        {
            RootContextData = { [TeamDisciplineEditDtoValidator.TournamentIdKey] = tournamentId },
        };

        var validationResult = await validator.ValidateAsync(context);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        dto = SanitizeEditDto(dto);

        var basedOn = await dbContext.Disciplines.Where(d => dto.BasedOn.Contains(d.Id)).ToListAsync();

        var teamDiscipline = new TeamDiscipline
        {
            Name = dto.Name,
            BasedOn = basedOn,
            TournamentId = tournamentId,
        };

        dbContext.Add(teamDiscipline);
        await dbContext.SaveChangesAsync();

        return Results.Ok(teamDiscipline.Id);
    }

    private static async Task<IResult> GetTeamDiscipline(ApplicationDbContext dbContext, int teamDisciplineId)
    {
        var teamDiscipline = await dbContext
            .TeamDisciplines.AsNoTracking()
            .Where(t => t.Id == teamDisciplineId)
            .Select(t => new TeamDisciplineDetailDto(
                t.Name,
                t.BasedOn.Select(d => new TeamDisciplineDetailDto.Discipline(d.Id, d.Name)).ToList()
            ))
            .FirstOrDefaultAsync();

        return teamDiscipline is null ? Results.NotFound() : Results.Ok(teamDiscipline);
    }

    private static async Task<IResult> UpdateTeamDiscipline(
        ApplicationDbContext dbContext,
        IValidator<TeamDisciplineEditDto> validator,
        int teamDisciplineId,
        TeamDisciplineEditDto dto
    )
    {
        var teamDiscipline = await dbContext
            .TeamDisciplines.Include(t => t.BasedOn)
            .FirstOrDefaultAsync(t => t.Id == teamDisciplineId);
        if (teamDiscipline is null)
            return Results.NotFound();

        var context = new ValidationContext<TeamDisciplineEditDto>(dto)
        {
            RootContextData = { [TeamDisciplineEditDtoValidator.TournamentIdKey] = teamDiscipline.TournamentId },
        };

        var validationResult = await validator.ValidateAsync(context);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        dto = SanitizeEditDto(dto);

        var basedOn = await dbContext.Disciplines.Where(d => dto.BasedOn.Contains(d.Id)).ToListAsync();

        teamDiscipline.Name = dto.Name;
        teamDiscipline.BasedOn.Clear();
        foreach (var discipline in basedOn)
        {
            teamDiscipline.BasedOn.Add(discipline);
        }

        await dbContext.SaveChangesAsync();

        return Results.Ok();
    }

    private static async Task<IResult> DeleteTeamDiscipline(ApplicationDbContext dbContext, int teamDisciplineId)
    {
        var teamDiscipline = await dbContext.TeamDisciplines.FindAsync(teamDisciplineId);
        if (teamDiscipline is null)
            return Results.NotFound();

        dbContext.Remove(teamDiscipline);
        await dbContext.SaveChangesAsync();

        return Results.Ok();
    }

    private static TeamDisciplineEditDto SanitizeEditDto(TeamDisciplineEditDto dto)
    {
        return dto with { Name = dto.Name.Trim() };
    }
}

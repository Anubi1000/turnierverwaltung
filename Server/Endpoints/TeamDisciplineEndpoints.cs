using FluentValidation;
using Microsoft.AspNetCore.Http.HttpResults;
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

        tournamentDependentGroup.MapGet("/", GetTeamDisciplines);

        tournamentDependentGroup.MapPost("/", CreateTeamDiscipline);

        tournamentIndependentGroup.MapGet("/", GetTeamDiscipline);

        tournamentIndependentGroup.MapPut("/", UpdateTeamDiscipline);

        tournamentIndependentGroup.MapDelete("/", DeleteTeamDiscipline);

        return builder;
    }

    private static async Task<Results<NotFound, Ok<List<ListTeamDisciplineDto>>>> GetTeamDisciplines(
        ApplicationDbContext dbContext,
        int tournamentId
    )
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return TypedResults.NotFound();

        var teamDisciplines = await dbContext
            .TeamDisciplines.Where(t => t.TournamentId == tournamentId)
            .OrderBy(t => t.Name)
            .ThenBy(t => t.Id)
            .Select(t => new ListTeamDisciplineDto(t.Id, t.Name))
            .ToListAsync();

        return TypedResults.Ok(teamDisciplines);
    }

    private static async Task<Results<NotFound, ValidationProblem, Ok<int>>> CreateTeamDiscipline(
        ApplicationDbContext dbContext,
        IValidator<TeamDisciplineEditDto> validator,
        int tournamentId,
        TeamDisciplineEditDto dto
    )
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return TypedResults.NotFound();

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

        return TypedResults.Ok(teamDiscipline.Id);
    }

    private static async Task<Results<NotFound, Ok<TeamDisciplineDetailDto>>> GetTeamDiscipline(
        ApplicationDbContext dbContext,
        int teamDisciplineId
    )
    {
        var teamDiscipline = await dbContext
            .TeamDisciplines.AsNoTracking()
            .Where(t => t.Id == teamDisciplineId)
            .Select(t => new TeamDisciplineDetailDto(
                t.Name,
                t.BasedOn.Select(d => new TeamDisciplineDetailDto.Discipline(d.Id, d.Name)).ToList()
            ))
            .FirstOrDefaultAsync();

        return teamDiscipline is null ? TypedResults.NotFound() : TypedResults.Ok(teamDiscipline);
    }

    private static async Task<Results<NotFound, ValidationProblem, Ok>> UpdateTeamDiscipline(
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
            return TypedResults.NotFound();

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

        return TypedResults.Ok();
    }

    private static async Task<Results<NotFound, Ok>> DeleteTeamDiscipline(
        ApplicationDbContext dbContext,
        int teamDisciplineId
    )
    {
        var teamDiscipline = await dbContext.TeamDisciplines.FindAsync(teamDisciplineId);
        if (teamDiscipline is null)
            return TypedResults.NotFound();

        dbContext.Remove(teamDiscipline);
        await dbContext.SaveChangesAsync();

        return TypedResults.Ok();
    }

    private static TeamDisciplineEditDto SanitizeEditDto(TeamDisciplineEditDto dto)
    {
        return dto with { Name = dto.Name.Trim() };
    }
}

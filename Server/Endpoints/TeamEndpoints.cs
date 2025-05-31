using FluentValidation;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using SharpGrip.FluentValidation.AutoValidation.Shared.Extensions;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Model.Transfer.Team;
using Turnierverwaltung.Server.Model.Validation;

namespace Turnierverwaltung.Server.Endpoints;

public static class TeamEndpoints
{
    public static IEndpointRouteBuilder MapTeamEndpoints(this IEndpointRouteBuilder builder)
    {
        var baseGroup = builder.MapGroup("/api").WithTags("Team").RequireAuthorization();

        var tournamentDependentGroup = baseGroup.MapGroup("/tournaments/{tournamentId:int}/teams");
        var tournamentIndependentGroup = baseGroup.MapGroup("/teams/{teamId:int}");

        // Tournament-based routes
        tournamentDependentGroup.MapGet("/", GetTeams).WithName("GetTeams");

        tournamentDependentGroup.MapPost("/", CreateTeam).WithName("CreateTeam");

        tournamentDependentGroup.MapGet("/nextStartNumber", GetNextTeamStartNumber).WithName("GetNextTeamStartNumber");

        // TeamRoutes
        tournamentIndependentGroup.MapGet("/", GetTeam).WithName("GetTeam");

        tournamentIndependentGroup.MapPut("/", UpdateTeam).WithName("UpdateTeam");

        tournamentIndependentGroup.MapDelete("/", DeleteTeam).WithName("DeleteTeam");

        return builder;
    }

    private static async Task<Results<NotFound, Ok<List<ListTeamDto>>>> GetTeams(
        ApplicationDbContext dbContext,
        int tournamentId
    )
    {
        if (!await dbContext.Tournaments.AsNoTracking().AnyAsync(t => t.Id == tournamentId))
            return TypedResults.NotFound();

        var teams = await dbContext
            .Teams.AsNoTracking()
            .Where(t => t.TournamentId == tournamentId)
            .OrderBy(t => t.StartNumber)
            .ThenBy(t => t.Name)
            .ThenBy(t => t.Id)
            .Select(t => new ListTeamDto(t.Id, t.Name, t.StartNumber))
            .ToListAsync();

        return TypedResults.Ok(teams);
    }

    private static async Task<Results<NotFound, ValidationProblem, Ok<int>>> CreateTeam(
        ApplicationDbContext dbContext,
        IValidator<TeamEditDto> validator,
        int tournamentId,
        TeamEditDto dto
    )
    {
        if (!await dbContext.Tournaments.AsNoTracking().AnyAsync(t => t.Id == tournamentId))
            return TypedResults.NotFound();

        var context = new ValidationContext<TeamEditDto>(dto)
        {
            RootContextData = { [TeamEditDtoValidator.TournamentIdKey] = tournamentId },
        };

        var validationResult = await validator.ValidateAsync(context);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        dto = SanitizeEditDto(dto);

        var members = await dbContext.Participants.Where(p => dto.Members.Contains(p.Id)).ToListAsync();
        var participatingDisciplines = await dbContext
            .TeamDisciplines.Where(d => dto.ParticipatingDisciplines.Contains(d.Id))
            .ToListAsync();

        var team = new Team
        {
            Name = dto.Name,
            StartNumber = dto.StartNumber,
            Members = members,
            ParticipatingDisciplines = participatingDisciplines,
            TournamentId = tournamentId,
        };

        dbContext.Teams.Add(team);
        await dbContext.SaveChangesAsync();

        return TypedResults.Ok(team.Id);
    }

    private static async Task<Results<NotFound, Ok<int>>> GetNextTeamStartNumber(ApplicationDbContext dbContext,
        int tournamentId)
    {
        if (!await dbContext.Tournaments.AsNoTracking().AnyAsync(t => t.Id == tournamentId))
            return TypedResults.NotFound();

        var currentMaxStartNumber = await dbContext.Teams.AsNoTracking()
            .Where(p => p.TournamentId == tournamentId)
            .MaxAsync(p => (int?) p.StartNumber);

        return TypedResults.Ok(currentMaxStartNumber.HasValue ? currentMaxStartNumber.Value + 1: 1);
    }

    private static async Task<Results<NotFound, Ok<TeamDetailDto>>> GetTeam(ApplicationDbContext dbContext, int teamId)
    {
        TeamDetailDto? team;
        await using (await dbContext.Database.BeginTransactionAsync())
        {
            team = await dbContext
                .Teams.AsNoTracking()
                .AsSplitQuery()
                .Where(t => t.Id == teamId)
                .Select(t => new TeamDetailDto(
                    t.Name,
                    t.StartNumber,
                    t.Members.Select(m => new TeamDetailDto.Member(m.Id, m.Name)).ToList(),
                    t.ParticipatingDisciplines.Select(d => new TeamDetailDto.TeamDiscipline(d.Id, d.Name)).ToList()
                ))
                .FirstOrDefaultAsync();
        }

        return team is null ? TypedResults.NotFound() : TypedResults.Ok(team);
    }

    private static async Task<Results<NotFound, ValidationProblem, Ok>> UpdateTeam(
        ApplicationDbContext dbContext,
        IValidator<TeamEditDto> validator,
        int teamId,
        TeamEditDto dto
    )
    {
        var team = await dbContext
            .Teams.Include(t => t.Members)
            .Include(t => t.ParticipatingDisciplines)
            .FirstOrDefaultAsync(t => t.Id == teamId);
        if (team is null)
            return TypedResults.NotFound();

        var context = new ValidationContext<TeamEditDto>(dto)
        {
            RootContextData = { [TeamEditDtoValidator.TournamentIdKey] = team.TournamentId },
        };

        var validationResult = await validator.ValidateAsync(context);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        dto = SanitizeEditDto(dto);

        var members = await dbContext.Participants.Where(p => dto.Members.Contains(p.Id)).ToListAsync();
        var participatingDisciplines = await dbContext
            .TeamDisciplines.Where(d => dto.ParticipatingDisciplines.Contains(d.Id))
            .ToListAsync();

        team.Name = dto.Name;
        team.StartNumber = dto.StartNumber;
        team.Members = members;
        team.ParticipatingDisciplines = participatingDisciplines;

        await dbContext.SaveChangesAsync();

        return TypedResults.Ok();
    }

    private static async Task<Results<NotFound, Ok>> DeleteTeam(ApplicationDbContext dbContext, int teamId)
    {
        var team = await dbContext.Teams.FindAsync(teamId);
        if (team is null)
            return TypedResults.NotFound();

        dbContext.Remove(team);
        await dbContext.SaveChangesAsync();

        return TypedResults.Ok();
    }

    private static TeamEditDto SanitizeEditDto(TeamEditDto dto)
    {
        return dto with { Name = dto.Name.Trim() };
    }
}

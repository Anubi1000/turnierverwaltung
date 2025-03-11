using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Model.Results;

namespace Turnierverwaltung.Server.Endpoints;

public static class OverviewEndpoints
{
    public static IEndpointRouteBuilder MapOverviewEndpoints(this IEndpointRouteBuilder builder)
    {
        var baseGroup = builder
            .MapGroup("/api/tournaments/{tournamentId:int}/overview")
            .WithTags("Overview")
            .RequireAuthorization();

        baseGroup.MapGet("/", GetOverviewData).Produces<ScoreboardData>().Produces(StatusCodes.Status404NotFound);

        return builder;
    }

    private static async Task<IResult> GetOverviewData(ApplicationDbContext dbContext, int tournamentId)
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return Results.NotFound();

        var scoreboardData = await ScoreboardDataCreator.GetScoreboardDataAsync(dbContext, tournamentId);

        return Results.Ok(scoreboardData);
    }
}

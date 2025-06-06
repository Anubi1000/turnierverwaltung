using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Hubs;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Endpoints;

public static class ScoreboardEndpoints
{
    public static IEndpointRouteBuilder MapScoreboardEndpoints(this IEndpointRouteBuilder builder)
    {
        var group = builder.MapGroup("/api/scoreboard").WithTags("Scoreboard");

        group.MapHub<ScoreboardHub>("/hub");

        group
            .MapPost("/tournament", SetScoreboardTournament)
            .RequireAuthorization()
            .WithName("SetScoreboardTournament");

        return builder;
    }

    private static async Task<Results<BadRequest<string>, Ok>> SetScoreboardTournament(
        ApplicationDbContext dbContext,
        IScoreboardManager scoreboardManager,
        [FromBody] int tournamentId
    )
    {
        if (tournamentId < 1)
        {
            await scoreboardManager.SetCurrentTournament(0);
            return TypedResults.Ok();
        }

        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return TypedResults.BadRequest("The specified tournament does not exist.");

        await scoreboardManager.SetCurrentTournament(tournamentId);
        return TypedResults.Ok();
    }
}

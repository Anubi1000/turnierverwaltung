using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Results.Scoreboard;
using Turnierverwaltung.Server.Results.Word;

namespace Turnierverwaltung.Server.Endpoints;

public static class OverviewEndpoints
{
    public static IEndpointRouteBuilder MapOverviewEndpoints(this IEndpointRouteBuilder builder)
    {
        var baseGroup = builder
            .MapGroup("/api/tournaments/{tournamentId:int}/overview")
            .WithTags("Overview")
            .RequireAuthorization();

        baseGroup.MapGet("/", GetOverviewData);

        baseGroup.MapGet("/download", GetScoreDocument);

        return builder;
    }

    private static async Task<Results<NotFound, Ok<ScoreboardData>>> GetOverviewData(
        ApplicationDbContext dbContext,
        int tournamentId
    )
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return TypedResults.NotFound();

        var scoreboardData = await ScoreboardDataCreator.CreateScoreboardDataAsync(dbContext, tournamentId);

        return TypedResults.Ok(scoreboardData);
    }

    private static async Task<IResult> GetScoreDocument(
        ApplicationDbContext dbContext,
        int tournamentId,
        [FromQuery] string? tables
    )
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return TypedResults.NotFound();

        var scoreboardData = await ScoreboardDataCreator.CreateScoreboardDataAsync(dbContext, tournamentId);
        if (scoreboardData is null)
            return TypedResults.NotFound();

        if (string.IsNullOrEmpty(tables))
        {
            var wordDoc = WordFileCreator.CreateWordScoreFile(scoreboardData);

            return TypedResults.File(
                fileContents: wordDoc,
                contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                fileDownloadName: "Ergebnisse.docx"
            );
        }

        throw new NotImplementedException("Separate documents are currently not implemented.");
    }
}

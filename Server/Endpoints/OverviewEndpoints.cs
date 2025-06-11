using System.Collections.Immutable;
using System.IO.Compression;
using FluentValidation;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SharpGrip.FluentValidation.AutoValidation.Shared.Extensions;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Model.Transfer;
using Turnierverwaltung.Server.Results.Scoreboard;
using Turnierverwaltung.Server.Results.Word;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Endpoints;

public static class OverviewEndpoints
{
    public static IEndpointRouteBuilder MapOverviewEndpoints(this IEndpointRouteBuilder builder)
    {
        var baseGroup = builder
            .MapGroup("/api/tournaments/{tournamentId:int}/overview")
            .WithTags("Tournament / Overview")
            .RequireAuthorization();

        baseGroup.MapGet("/", GetOverviewData).WithName("GetTournamentOverview");

        baseGroup.MapPost("/download", GenerateWordScoreDocument).WithName("GetTournamentScoreboardAsWord");

        return builder;
    }

    private static async Task<Results<NotFound, Ok<ScoreboardData>>> GetOverviewData(
        [FromServices] ApplicationDbContext dbContext,
        [FromServices] IScoreboardDataCreator scoreboardDataCreator,
        [FromRoute] int tournamentId
    )
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return TypedResults.NotFound();

        var scoreboardData = await scoreboardDataCreator.CreateScoreboardDataAsync(tournamentId);

        return scoreboardData is null ? TypedResults.NotFound() : TypedResults.Ok(scoreboardData);
    }

    private static async Task<Results<NotFound, ValidationProblem, FileStreamHttpResult>> GenerateWordScoreDocument(
        [FromServices] ApplicationDbContext dbContext,
        [FromServices] IValidator<WordDocGenerationDto> validator,
        [FromServices] IScoreboardDataCreator scoreboardDataCreator,
        [FromServices] IWordFileCreator wordFileCreator,
        [FromRoute] int tournamentId,
        [FromBody] WordDocGenerationDto dto
    )
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return TypedResults.NotFound();

        var scoreboardData = await scoreboardDataCreator.CreateScoreboardDataAsync(tournamentId);
        if (scoreboardData is null)
            return TypedResults.NotFound();

        // Validate dto and return ValidationProblem if not valid
        var validationResult = await validator.ValidateAsync(dto);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        var filteredData = scoreboardData with
        {
            Tables = scoreboardData.Tables.Where(table => dto.TablesToExport.Contains(table.Id)).ToImmutableList(),
        };

        // If only one table requested it can be returned directly as a document
        if (dto.SeparateDocuments && filteredData.Tables.Count > 1)
        {
            var zipStream = new MemoryStream();

            using (var archive = new ZipArchive(zipStream, ZipArchiveMode.Create, true))
            {
                for (var tableIndex = 0; tableIndex < filteredData.Tables.Count; tableIndex++)
                {
                    var entry = archive.CreateEntry($"{filteredData.Tables[tableIndex].Name}.docx");
                    await using var entryStream = entry.Open();

                    using var fileStream = await wordFileCreator.CreateWordFileForTableAsStream(
                        filteredData,
                        tableIndex
                    );
                    await fileStream.CopyToAsync(entryStream);
                }
            }

            zipStream.Seek(0, SeekOrigin.Begin);
            return TypedResults.Stream(zipStream, MimeTypes.Zip, $"{filteredData.TournamentName}.zip");
        }

        var wordDocStream = await wordFileCreator.CreateWordFileAsStream(filteredData);

        return TypedResults.Stream(wordDocStream, MimeTypes.Docx, $"{filteredData.TournamentName}.docx");
    }
}

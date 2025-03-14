using System.Collections.Frozen;
using System.Collections.Immutable;
using System.Globalization;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Results.Scoreboard;

public partial class ScoreboardDataCreator(ApplicationDbContext dbContext, int tournamentId)
{
    private const int ScoreRoundingPrecision = 10;

    private static readonly CultureInfo GermanCultureInfo = CultureInfo.GetCultureInfo("de-DE");
    private static readonly DecimalArrayComparer DecimalArrayComparer = new();
    private readonly List<ScoreboardData.Table> _tables = [];

    private FrozenDictionary<ParticipantResult, decimal[]> _calculatedResults = FrozenDictionary<
        ParticipantResult,
        decimal[]
    >.Empty;

    private Tournament _tournament = null!;

    public static Task<ScoreboardData?> CreateScoreboardDataAsync(ApplicationDbContext dbContext, int tournamentId)
    {
        var creator = new ScoreboardDataCreator(dbContext, tournamentId);
        return creator.CreateScoreboardData();
    }

    private async Task<ScoreboardData?> CreateScoreboardData()
    {
        Tournament? tournament;
        await using (await dbContext.Database.BeginTransactionAsync())
        {
            tournament = await dbContext
                .Tournaments
                // Disciplines
                .Include(t => t.Disciplines)
                // TeamDisciplines > Disciplines
                .Include(t => t.TeamDisciplines)
                .ThenInclude(d => d.BasedOn)
                // Participants > Results > Discipline
                .Include(t => t.Participants)
                .ThenInclude(p => p.Results)
                .ThenInclude(pr => pr.Discipline)
                // Participant > Club
                .Include(t => t.Participants)
                .ThenInclude(p => p.Club)
                // Teams > Members
                .Include(t => t.Teams)
                .ThenInclude(t => t.Members)
                // End
                .AsSplitQuery()
                .FirstOrDefaultAsync(t => t.Id == tournamentId);
            await dbContext.Database.CommitTransactionAsync();
        }

        if (tournament is null)
            return null;

        _tournament = tournament;
        _calculatedResults = tournament
            .Participants.SelectMany(p => p.Results)
            .ToFrozenDictionary(result => result, GetScoresFromResult);

        AddDisciplineTables();
        AddTeamDisciplineTables();

        return new ScoreboardData(_tournament.Name, _tables.ToImmutableList());
    }

    private static decimal[] GetScoresFromResult(ParticipantResult result)
    {
        return result
            .Rounds.Select(round =>
                round
                    .Values.Zip(
                        result.Discipline.Values,
                        (value, disciplineValue) =>
                            disciplineValue.IsAdded ? Convert.ToDecimal(value) : -Convert.ToDecimal(value)
                    )
                    .Sum()
            )
            .ToArray();
    }
}

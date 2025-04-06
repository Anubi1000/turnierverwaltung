using System.Collections.Frozen;
using System.Collections.Immutable;
using System.Globalization;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;

namespace Turnierverwaltung.Server.Results.Scoreboard;

public partial class ScoreboardDataCreator(ApplicationDbContext dbContext) : IScoreboardDataCreator
{
    private const int ScoreRoundingPrecision = 10;

    private static readonly CultureInfo GermanCultureInfo = CultureInfo.GetCultureInfo("de-DE");

    public async Task<ScoreboardData?> CreateScoreboardDataAsync(int tournamentId)
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

        var calculatedResults = tournament
            .Participants.SelectMany(p => p.Results)
            .ToFrozenDictionary(result => result, GetScoresFromResult);

        var tables = new List<ScoreboardData.Table>();
        CreateDisciplineTables(tournament, calculatedResults, tables);
        CreateTeamDisciplineTables(tournament, calculatedResults, tables);

        return new ScoreboardData(tournament.Name, tables.ToImmutableList());
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

    private static void CreateTeamDisciplineTables(
        Tournament tournament,
        FrozenDictionary<ParticipantResult, decimal[]> calculatedResults,
        List<ScoreboardData.Table> tables
    )
    {
        foreach (var teamDiscipline in tournament.TeamDisciplines)
            switch (teamDiscipline.DisplayType)
            {
                case TeamDiscipline.ScoreDisplayType.Normal:
                    CreateTeamDisciplineTableNormal(tournament, teamDiscipline, calculatedResults, tables);
                    break;
                case TeamDiscipline.ScoreDisplayType.Nationcup:
                    CreateTeamDisciplineTableNationcup(tournament, teamDiscipline, calculatedResults, tables);
                    break;
                default:
                    throw new ArgumentOutOfRangeException();
            }
    }
}

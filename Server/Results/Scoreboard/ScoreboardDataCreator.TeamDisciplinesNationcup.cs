using System.Collections.Frozen;
using System.Collections.Immutable;
using Turnierverwaltung.Server.Database.Model;

namespace Turnierverwaltung.Server.Results.Scoreboard;

public partial class ScoreboardDataCreator
{
    private const decimal MaxPointsPerRound = 50.5m;

    private static void CreateTeamDisciplineTableNationcup(
        Tournament tournament,
        TeamDiscipline teamDiscipline,
        FrozenDictionary<ParticipantResult, decimal[]> calculatedResults,
        List<ScoreboardData.Table> tables
    )
    {
        var columns = CreateColumnsForTeamDisciplineNationcup();

        var rows = teamDiscipline
            .ParticipatingTeams.Where(team => team.Members.Count >= tournament.TeamSize)
            .Select(team =>
            {
                var allResults = calculatedResults
                    .Where(pair =>
                        teamDiscipline.BasedOn.Contains(pair.Key.Discipline)
                        && team.Members.Contains(pair.Key.Participant)
                    )
                    .ToList();

                var totalRounds = allResults.Sum(pair => pair.Value.Length);
                var maxPoints = totalRounds * MaxPointsPerRound;
                var reachedPoints = allResults.Sum(pair => pair.Value.Sum());
                var percentage = reachedPoints / maxPoints;

                return (team, totalRounds, maxPoints, reachedPoints, percentage);
            })
            .OrderByDescending(result => result.percentage)
            .Select(
                (result, index) =>
                {
                    var (team, totalRounds, maxPoints, reachedPoints, percentage) = result;

                    var values = ImmutableList.Create(
                        (index + 1).ToString(),
                        team.Name,
                        totalRounds.ToString(),
                        Math.Round(maxPoints, ScoreRoundingPrecision).ToString(GermanCultureInfo),
                        Math.Round(reachedPoints, ScoreRoundingPrecision).ToString(GermanCultureInfo),
                        Math.Round(percentage * 100, 2).ToString(GermanCultureInfo) + " %"
                    );

                    return new ScoreboardData.Table.Row(team.Id, values);
                }
            )
            .ToImmutableList();

        tables.Add(new ScoreboardData.Table(teamDiscipline.Name, columns, rows));
    }

    private static ImmutableList<ScoreboardData.Table.Column> CreateColumnsForTeamDisciplineNationcup()
    {
        var columns = ImmutableList.Create(
            new ScoreboardData.Table.Column(
                "Platz",
                new ScoreboardData.Table.Column.IWidth.Fixed(125),
                ScoreboardData.Table.Column.Alignment.Center
            ),
            new ScoreboardData.Table.Column(
                "Nation",
                new ScoreboardData.Table.Column.IWidth.Variable(1),
                ScoreboardData.Table.Column.Alignment.Left
            ),
            new ScoreboardData.Table.Column(
                "Summe Serien",
                new ScoreboardData.Table.Column.IWidth.Fixed(250),
                ScoreboardData.Table.Column.Alignment.Center
            ),
            new ScoreboardData.Table.Column(
                "Mögliche Ringe",
                new ScoreboardData.Table.Column.IWidth.Fixed(250),
                ScoreboardData.Table.Column.Alignment.Center
            ),
            new ScoreboardData.Table.Column(
                "Erreichte Ringe",
                new ScoreboardData.Table.Column.IWidth.Fixed(250),
                ScoreboardData.Table.Column.Alignment.Center
            ),
            new ScoreboardData.Table.Column(
                "Trefferquote",
                new ScoreboardData.Table.Column.IWidth.Fixed(250),
                ScoreboardData.Table.Column.Alignment.Right
            )
        );

        return columns;
    }
}

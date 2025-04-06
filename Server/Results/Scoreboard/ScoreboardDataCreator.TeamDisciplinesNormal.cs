using System.Collections.Frozen;
using System.Collections.Immutable;
using Turnierverwaltung.Server.Database.Model;

namespace Turnierverwaltung.Server.Results.Scoreboard;

public partial class ScoreboardDataCreator
{
    private static void CreateTeamDisciplineTableNormal(
        Tournament tournament,
        TeamDiscipline teamDiscipline,
        FrozenDictionary<ParticipantResult, decimal[]> calculatedResults,
        List<ScoreboardData.Table> tables
    )
    {
        var columns = CreateColumnsForTeamDisciplineNormal(tournament.TeamSize);

        var disciplineIds = teamDiscipline.BasedOn.Select(d => d.Id).ToList();

        var rows = teamDiscipline
            .ParticipatingTeams.Where(team => team.Members.Count >= tournament.TeamSize)
            .Select(team =>
            {
                var memberScores = team.Members.ToDictionary(
                    member => member,
                    member =>
                        calculatedResults
                            .Where(result =>
                                result.Key.ParticipantId == member.Id && disciplineIds.Contains(result.Key.DisciplineId)
                            )
                            .Select(r => r.Value.Max())
                            .DefaultIfEmpty(0)
                            .Max()
                );

                var totalScore = memberScores.Values.Sum();
                return (team, memberScores, totalScore);
            })
            .OrderByDescending(result => result.totalScore)
            .Select(
                (result, index) =>
                {
                    var (team, memberScores, totalScore) = result;

                    var values = new List<string>(4 + memberScores.Count * 2)
                    {
                        (index + 1).ToString(),
                        team.StartNumber.ToString(),
                        team.Name,
                    };

                    foreach (var (member, score) in memberScores)
                    {
                        values.Add(member.Name);
                        values.Add(Math.Round(score, ScoreRoundingPrecision).ToString(GermanCultureInfo));
                    }

                    values.Add(Math.Round(totalScore, ScoreRoundingPrecision).ToString(GermanCultureInfo));

                    return new ScoreboardData.Table.Row(team.Id, values.ToImmutableList());
                }
            )
            .ToImmutableList();

        tables.Add(new ScoreboardData.Table(teamDiscipline.Name, columns, rows));
    }

    private static ImmutableList<ScoreboardData.Table.Column> CreateColumnsForTeamDisciplineNormal(int teamSize)
    {
        var columns = new List<ScoreboardData.Table.Column>(4 + teamSize * 2)
        {
            new(
                "Platz",
                new ScoreboardData.Table.Column.IWidth.Fixed(125),
                ScoreboardData.Table.Column.Alignment.Center
            ),
            new(
                "Startnummer",
                new ScoreboardData.Table.Column.IWidth.Fixed(125),
                ScoreboardData.Table.Column.Alignment.Center
            ),
            new("Name", new ScoreboardData.Table.Column.IWidth.Variable(1), ScoreboardData.Table.Column.Alignment.Left),
        };

        for (var i = 1; i <= teamSize; i++)
        {
            columns.Add(
                new ScoreboardData.Table.Column(
                    $"Schütze {i}",
                    new ScoreboardData.Table.Column.IWidth.Variable(1),
                    ScoreboardData.Table.Column.Alignment.Right
                )
            );
            columns.Add(
                new ScoreboardData.Table.Column(
                    "Punkte",
                    new ScoreboardData.Table.Column.IWidth.Fixed(200),
                    ScoreboardData.Table.Column.Alignment.Left
                )
            );
        }

        columns.Add(
            new ScoreboardData.Table.Column(
                "Gesamt",
                new ScoreboardData.Table.Column.IWidth.Fixed(200),
                ScoreboardData.Table.Column.Alignment.Right
            )
        );

        return columns.ToImmutableList();
    }
}

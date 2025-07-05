using System.Collections.Frozen;
using System.Collections.Immutable;
using Turnierverwaltung.Server.Database.Model;

namespace Turnierverwaltung.Server.Results.Scoreboard;

public partial class ScoreboardDataCreator
{
    /// <summary>
    ///     Creates the scoreboard table for a team discipline with the display type "Triathlon".
    /// </summary>
    /// <param name="tournament">The tournament the team discipline belongs to.</param>
    /// <param name="teamDiscipline">The team discipline to create the table for.</param>
    /// <param name="calculatedResults">The pre-calculated round results for all participant results.</param>
    /// <param name="tables">The list of tables to which the generated table will be added.</param>
    private static void CreateTeamDisciplineTableTriathlon(
        Tournament tournament,
        TeamDiscipline teamDiscipline,
        FrozenDictionary<ParticipantResult, decimal[]> calculatedResults,
        List<ScoreboardData.Table> tables
    )
    {
        // Create the column definitions for the table.
        var columns = CreateColumnsForTeamDisciplineNormal(tournament.TeamSize);

        // Filter results relevant to the current team discipline.
        var relevantResults = calculatedResults
            .Where(pair => teamDiscipline.BasedOn.Contains(pair.Key.Discipline))
            .ToList();

        var resultsByParticipant = calculatedResults
            .Where(pair => teamDiscipline.BasedOn.Contains(pair.Key.Discipline))
            .ToLookup(pair => pair.Key.Participant, pair => pair.Value);

        // Generate rows for each participating team.
        var rows = teamDiscipline
            .ParticipatingTeams.Where(team => team.Members.Count >= tournament.TeamSize)
            .Select(team =>
            {
                // Determine the minimum number of rounds across all team members.
                var minRounds = resultsByParticipant
                    .Where(group => team.Members.Contains(group.Key))
                    .SelectMany(group => group)
                    .DefaultIfEmpty([])
                    .Min(array => array.Length);

                if (minRounds == 0)
                    return (team, memberScores: [], totalScore: 0m);

                // Calculate scores for each round and identify the best round based on the total score.
                var bestRound = Enumerable
                    .Range(0, minRounds)
                    .Select(roundIndex =>
                    {
                        // Calculate scores for each team member in the current round.
                        var memberScores = team
                            .Members.Select(member =>
                            {
                                var score = resultsByParticipant[member].Sum(scores => scores[roundIndex]);

                                return (member, score);
                            })
                            // Only use the best members in the team for calculation.
                            .OrderByDescending(r => r.score)
                            .Take(tournament.TeamSize)
                            .ToList();

                        // Calculate the total score for the round.
                        var totalScore = memberScores.Sum(ms => ms.score);

                        return (memberScores, totalScore);
                    })
                    .MaxBy(r => r.totalScore);

                return (team, bestRound.memberScores, bestRound.totalScore);
            })
            // Filter out teams with no results.
            .Where(result => result.memberScores.Count > 0)
            // Sort teams by their total score in descending order.
            .OrderByDescending(result => result.totalScore)
            // Map each team to a table row.
            .Select(
                (result, index) =>
                {
                    var (team, memberScores, totalScore) = result;

                    // Prepare the row values.
                    var values = new List<string>(4 + memberScores.Count * 2)
                    {
                        (index + 1).ToString(), // Rank
                        team.StartNumber.ToString(), // Team start number
                        team.Name, // Team name
                    };

                    // Add each member's name and score to the row.
                    foreach (var (member, score) in memberScores)
                    {
                        values.Add(member.Name);
                        values.Add(FormatScore(score));
                    }

                    // Add the total score to the row.
                    values.Add(FormatScore(totalScore));

                    return new ScoreboardData.Table.Row(team.Id, values.ToImmutableList());
                }
            )
            .ToImmutableList();

        // Add the generated table to the list of tables.
        tables.Add(new ScoreboardData.Table(teamDiscipline.Id.ToString(), teamDiscipline.Name, columns, rows));
    }
}

using System.Collections.Immutable;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Results.Scoreboard;

public partial class ScoreboardDataCreator
{
    private void AddDisciplineTables()
    {
        var columnCache = new Dictionary<int, ImmutableList<ScoreboardData.Table.Column>>();

        foreach (var discipline in _tournament.Disciplines)
        {
            var columns = columnCache.GetOrCompute(discipline.AmountOfBestRoundsToShow, CreateColumnsForDiscipline);

            var participantScores = _tournament
                .Participants.Select(participant =>
                {
                    var result = participant.Results.FirstOrDefault(result => result.DisciplineId == discipline.Id);
                    var scores = result is not null ? _calculatedResults[result] : [];
                    return (participant, scores);
                })
                .Where(result => result.scores.Length != 0)
                .OrderByDescending(result => result.scores, DecimalArrayComparer);

            if (discipline.AreGendersSeparated)
            {
                var immediateScores = participantScores.ToList();

                foreach (var gender in Enum.GetValues<Gender>())
                    CreateDisciplineTable(
                        discipline.Name + (gender == Gender.Male ? "(m)" : "(w)"),
                        discipline.AmountOfBestRoundsToShow,
                        immediateScores.Where(result => result.participant.Gender == gender),
                        columns
                    );
            }
            else
            {
                CreateDisciplineTable(discipline.Name, discipline.AmountOfBestRoundsToShow, participantScores, columns);
            }
        }
    }

    private void CreateDisciplineTable(
        string name,
        int roundsToShow,
        IEnumerable<(Participant participant, decimal[] scores)> participantScores,
        ImmutableList<ScoreboardData.Table.Column> columns
    )
    {
        var rows = participantScores
            .Select((result, index) => CreateDisciplineRow(result.participant, result.scores, index, roundsToShow))
            .ToImmutableList();

        var table = new ScoreboardData.Table(name, columns, rows);
        _tables.Add(table);
    }

    private static ScoreboardData.Table.Row CreateDisciplineRow(
        Participant participant,
        decimal[] scores,
        int index,
        int roundsToShow
    )
    {
        var values = new List<string>(4 + roundsToShow)
        {
            (index + 1).ToString(),
            participant.StartNumber.ToString(),
            participant.Name,
            participant.Club.Name,
        };

        for (var i = 0; i < roundsToShow; i++)
        {
            var score = "";
            if (i < scores.Length)
                score = Math.Round(scores[i], ScoreRoundingPrecision).ToString(GermanCultureInfo);

            values.Add(score);
        }

        return new ScoreboardData.Table.Row(participant.Id, values.ToImmutableList());
    }

    private static ImmutableList<ScoreboardData.Table.Column> CreateColumnsForDiscipline(int amountOfRoundsToShow)
    {
        var columns = new List<ScoreboardData.Table.Column>(4 + amountOfRoundsToShow)
        {
            new(
                "Platz",
                new ScoreboardData.Table.Column.IWidth.Fixed(100),
                ScoreboardData.Table.Column.Alignment.Center
            ),
            new(
                "Startnummer",
                new ScoreboardData.Table.Column.IWidth.Fixed(250),
                ScoreboardData.Table.Column.Alignment.Center
            ),
            new("Name", new ScoreboardData.Table.Column.IWidth.Variable(1), ScoreboardData.Table.Column.Alignment.Left),
            new(
                "Verein",
                new ScoreboardData.Table.Column.IWidth.Variable(1),
                ScoreboardData.Table.Column.Alignment.Left
            ),
        };

        var pointsColumnWidth = new ScoreboardData.Table.Column.IWidth.Fixed(150);
        const ScoreboardData.Table.Column.Alignment pointsColumnAlignment = ScoreboardData.Table.Column.Alignment.Right;

        if (amountOfRoundsToShow is 1)
            columns.Add(new ScoreboardData.Table.Column("Punkte", pointsColumnWidth, pointsColumnAlignment));
        else
            for (var i = 1; i <= amountOfRoundsToShow; i++)
                columns.Add(new ScoreboardData.Table.Column($"Runde {i}", pointsColumnWidth, pointsColumnAlignment));

        return columns.ToImmutableList();
    }
}

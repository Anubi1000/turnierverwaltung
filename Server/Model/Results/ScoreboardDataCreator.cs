using System.Collections.Frozen;
using System.Collections.Immutable;
using System.Globalization;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Model.Results;

public class ScoreboardDataCreator(ApplicationDbContext dbContext, int tournamentId)
{
    private static readonly CultureInfo GermanCultureInfo = CultureInfo.GetCultureInfo("de-DE");
    private static readonly DoubleArrayComparer DoubleArrayComparer = new DoubleArrayComparer();
    private readonly List<ScoreboardData.Table> _tables = [];

    private Tournament _tournament = null!;
    private FrozenDictionary<ParticipantResult, double[]> _calculatedResults = FrozenDictionary<
        ParticipantResult,
        double[]
    >.Empty;

    public static Task<ScoreboardData?> GetScoreboardDataAsync(ApplicationDbContext dbContext, int tournamentId)
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
                .Tournaments.Include(t => t.Disciplines)
                .Include(t => t.Participants)
                .ThenInclude(p => p.Results)
                .ThenInclude(pr => pr.Discipline)
                .Include(t => t.Participants)
                .ThenInclude(p => p.Club)
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

        return new ScoreboardData(_tournament.Name, _tables.ToImmutableList());
    }

    private void AddDisciplineTables()
    {
        foreach (var discipline in _tournament.Disciplines)
        {
            var columns = CreateColumnsForDiscipline(discipline);

            var participantScores = _tournament
                .Participants.Select(participant =>
                {
                    var result = participant.Results.FirstOrDefault(result => result.DisciplineId == discipline.Id);
                    var scores = result is not null ? _calculatedResults[result] : [];
                    return (participant, scores);
                })
                .Where(result => result.scores.Length != 0)
                .OrderByDescending(result => result.scores, DoubleArrayComparer);

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
        IEnumerable<(Participant participant, double[] scores)> participantScores,
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
        double[] scores,
        int index,
        int roundsToShow
    )
    {
        var values = new List<string>
        {
            (index + 1).ToString(),
            participant.StartNumber.ToString(),
            participant.Name,
            participant.Club.Name,
        };

        for (var i = 0; i < roundsToShow; i++)
            values.Add(i < scores.Length ? scores[i].ToString(GermanCultureInfo) : "");

        return new ScoreboardData.Table.Row(participant.Id, values.ToImmutableList());
    }

    private static ImmutableList<ScoreboardData.Table.Column> CreateColumnsForDiscipline(Discipline discipline)
    {
        var columns = new List<ScoreboardData.Table.Column>
        {
            new(
                "Platz",
                new ScoreboardData.Table.Column.Width.Fixed(125),
                ScoreboardData.Table.Column.Alignment.Center
            ),
            new(
                "Startnummer",
                new ScoreboardData.Table.Column.Width.Fixed(125),
                ScoreboardData.Table.Column.Alignment.Center
            ),
            new("Name", new ScoreboardData.Table.Column.Width.Variable(1), ScoreboardData.Table.Column.Alignment.Left),
            new(
                "Verein",
                new ScoreboardData.Table.Column.Width.Variable(1),
                ScoreboardData.Table.Column.Alignment.Left
            ),
        };

        var pointsColumnWidth = new ScoreboardData.Table.Column.Width.Fixed(150);
        const ScoreboardData.Table.Column.Alignment pointsColumnAlignment = ScoreboardData.Table.Column.Alignment.Right;

        if (discipline.AmountOfBestRoundsToShow == 1)
        {
            columns.Add(new ScoreboardData.Table.Column("Punkte", pointsColumnWidth, pointsColumnAlignment));
        }
        else
        {
            for (var i = 1; i <= discipline.AmountOfBestRoundsToShow; i++)
                columns.Add(new ScoreboardData.Table.Column($"Runde {i}", pointsColumnWidth, pointsColumnAlignment));
        }

        return columns.ToImmutableList();
    }

    private static double[] GetScoresFromResult(ParticipantResult result)
    {
        return result
            .Rounds.Select(round =>
                round
                    .Values.Zip(
                        result.Discipline.Values,
                        (value, disciplineValue) => disciplineValue.IsAdded ? value : -value
                    )
                    .Sum()
            )
            .ToArray();
    }
}

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
    private const int ScoreRoundingPrecision = 10;

    private static readonly CultureInfo GermanCultureInfo = CultureInfo.GetCultureInfo("de-DE");
    private static readonly DecimalArrayComparer DecimalArrayComparer = new();
    private readonly List<ScoreboardData.Table> _tables = [];

    private Tournament _tournament = null!;
    private FrozenDictionary<ParticipantResult, decimal[]> _calculatedResults = FrozenDictionary<
        ParticipantResult,
        decimal[]
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

    #region Disciplines

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
            {
                score = Math.Round(scores[i], ScoreRoundingPrecision).ToString(GermanCultureInfo);
            }

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

        if (amountOfRoundsToShow is 1)
        {
            columns.Add(new ScoreboardData.Table.Column("Punkte", pointsColumnWidth, pointsColumnAlignment));
        }
        else
        {
            for (var i = 1; i <= amountOfRoundsToShow; i++)
                columns.Add(new ScoreboardData.Table.Column($"Runde {i}", pointsColumnWidth, pointsColumnAlignment));
        }

        return columns.ToImmutableList();
    }

    #endregion

    #region TeamDisciplines

    private void AddTeamDisciplineTables()
    {
        var columns = CreateColumnsForTeamDiscipline();

        foreach (var teamDiscipline in _tournament.TeamDisciplines)
        {
            var disciplineIds = teamDiscipline.BasedOn.Select(d => d.Id).ToList();

            var rows = teamDiscipline
                .ParticipatingTeams.Select(team =>
                {
                    var memberScores = team.Members.ToDictionary(
                        member => member,
                        member =>
                            _calculatedResults
                                .Where(result =>
                                    result.Key.ParticipantId == member.Id
                                    && disciplineIds.Contains(result.Key.DisciplineId)
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

                        var values = new List<string>(4 + memberScores.Count)
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

            var table = new ScoreboardData.Table(teamDiscipline.Name, columns, rows);
            _tables.Add(table);
        }
    }

    private ImmutableList<ScoreboardData.Table.Column> CreateColumnsForTeamDiscipline()
    {
        var columns = new List<ScoreboardData.Table.Column>(4 + _tournament.TeamSize * 2)
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
        };

        for (var i = 1; i <= _tournament.TeamSize; i++)
        {
            columns.Add(
                new ScoreboardData.Table.Column(
                    $"Schütze {i}",
                    new ScoreboardData.Table.Column.Width.Variable(1),
                    ScoreboardData.Table.Column.Alignment.Right
                )
            );
            columns.Add(
                new ScoreboardData.Table.Column(
                    "Punkte",
                    new ScoreboardData.Table.Column.Width.Fixed(200),
                    ScoreboardData.Table.Column.Alignment.Left
                )
            );
        }

        columns.Add(
            new ScoreboardData.Table.Column(
                "Gesamt",
                new ScoreboardData.Table.Column.Width.Fixed(200),
                ScoreboardData.Table.Column.Alignment.Right
            )
        );

        return columns.ToImmutableList();
    }

    #endregion

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

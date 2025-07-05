using Turnierverwaltung.Server.Results.Scoreboard;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Tests.Utils;

public sealed class NoOpScoreboardManager : IScoreboardManager
{
    public static readonly NoOpScoreboardManager Instance = new();

    public int CurrentTournamentId => 0;
    public ScoreboardData? LatestScoreboardData => null;

    public Task SetCurrentTournament(int tournamentId)
    {
        return Task.CompletedTask;
    }

    public void NotifyUpdate(int tournamentId) { }
}

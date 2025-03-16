namespace Turnierverwaltung.Server.Results.Scoreboard;

public interface IScoreboardDataCreator
{
    public Task<ScoreboardData?> CreateScoreboardDataAsync(int tournamentId);
}

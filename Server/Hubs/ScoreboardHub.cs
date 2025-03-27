using Microsoft.AspNetCore.SignalR;
using Turnierverwaltung.Server.Results.Scoreboard;

namespace Turnierverwaltung.Server.Hubs;

public class ScoreboardHub : Hub<ScoreboardHub.IScoreboardClient>
{
    public interface IScoreboardClient
    {
        Task SendUpdate(ScoreboardData scoreboardData);
        Task ClearScoreboard();
    }
}

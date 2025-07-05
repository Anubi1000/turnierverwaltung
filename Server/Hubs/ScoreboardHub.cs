using Microsoft.AspNetCore.SignalR;
using Turnierverwaltung.Server.Results.Scoreboard;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Hubs;

public class ScoreboardHub(IScoreboardManager scoreboardManager) : Hub<ScoreboardHub.IScoreboardClient>
{
    public override Task OnConnectedAsync()
    {
        var data = scoreboardManager.LatestScoreboardData;
        if (data is not null)
            Clients.Caller.SendUpdate(data);

        return base.OnConnectedAsync();
    }

    public interface IScoreboardClient
    {
        Task SendUpdate(ScoreboardData scoreboardData);
        Task ClearScoreboard();
    }
}

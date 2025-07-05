using Microsoft.AspNetCore.SignalR;
using Turnierverwaltung.Server.Hubs;
using Turnierverwaltung.Server.Results.Scoreboard;

namespace Turnierverwaltung.Server.Utils;

public interface IScoreboardManager
{
    public int CurrentTournamentId { get; }
    public ScoreboardData? LatestScoreboardData { get; }
    public Task SetCurrentTournament(int tournamentId);
    public void NotifyUpdate(int tournamentId);
}

public sealed class ScoreboardManager(
    ILogger<ScoreboardManager> logger,
    IHubContext<ScoreboardHub, ScoreboardHub.IScoreboardClient> scoreboardHub,
    IServiceProvider serviceProvider
) : IScoreboardManager
{
    private readonly Lock _updateLock = new();
    private bool _isUpdateQueued;
    private bool _isUpdateRunning;

    public ScoreboardData? LatestScoreboardData { get; private set; }

    public int CurrentTournamentId { get; private set; }

    public async Task SetCurrentTournament(int tournamentId)
    {
        CurrentTournamentId = tournamentId;
        await NotifyUpdateInternal();
    }

    public void NotifyUpdate(int tournamentId)
    {
        if (tournamentId != CurrentTournamentId)
            return;

        lock (_updateLock)
        {
            if (_isUpdateRunning)
            {
                _isUpdateQueued = true;
                return;
            }

            _isUpdateRunning = true;
        }

        Task.Run(async () => await RunNotifyUpdateInternal());
    }

    private async Task RunNotifyUpdateInternal()
    {
        try
        {
            await NotifyUpdateInternal();
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "Failed to update scoreboard");
        }
        finally
        {
            bool runAgain;

            lock (_updateLock)
            {
                if (_isUpdateQueued)
                {
                    _isUpdateQueued = false;
                    runAgain = true;
                }
                else
                {
                    _isUpdateRunning = false;
                    runAgain = false;
                }
            }

            if (runAgain)
                await RunNotifyUpdateInternal();
        }
    }

    private async Task NotifyUpdateInternal()
    {
        using var scope = serviceProvider.CreateScope();
        var scoreboardDataCreator = scope.ServiceProvider.GetRequiredService<IScoreboardDataCreator>();
        var data = await scoreboardDataCreator.CreateScoreboardDataAsync(CurrentTournamentId);

        if (data == null)
        {
            CurrentTournamentId = 0;
            LatestScoreboardData = null;
            logger.LogDebug("Sending clear notification to scoreboard");
            await scoreboardHub.Clients.All.ClearScoreboard();
            return;
        }

        LatestScoreboardData = data;
        logger.LogDebug("Sending updated data to scoreboard");
        await scoreboardHub.Clients.All.SendUpdate(data);
    }
}

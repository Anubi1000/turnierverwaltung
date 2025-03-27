using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Database.Notification;
using Turnierverwaltung.Server.Hubs;
using Turnierverwaltung.Server.Results.Scoreboard;

namespace Turnierverwaltung.Server.Utils;

public interface IScoreboardManager
{
    public int CurrentTournamentId { get; }
    public Task SetCurrentTournament(int tournamentId);
}

public class ScoreboardManager : IScoreboardManager, IDisposable
{
    private readonly IEntityChangeNotifier _changeNotifier;
    private readonly IHubContext<ScoreboardHub, ScoreboardHub.IScoreboardClient> _scoreboardHub;
    private readonly IServiceProvider _serviceProvider;

    public ScoreboardManager(
        IServiceProvider serviceProvider,
        IEntityChangeNotifier changeNotifier,
        IHubContext<ScoreboardHub, ScoreboardHub.IScoreboardClient> scoreboardHub
    )
    {
        _serviceProvider = serviceProvider;
        _changeNotifier = changeNotifier;
        _scoreboardHub = scoreboardHub;

        changeNotifier.RegisterAsyncListener(DatabaseUpdateListener);
    }

    public void Dispose()
    {
        _changeNotifier.UnregisterListener(DatabaseUpdateListener);
    }

    public int CurrentTournamentId { get; private set; }

    public async Task SetCurrentTournament(int tournamentId)
    {
        CurrentTournamentId = tournamentId;
        using var scope = _serviceProvider.CreateScope();
        await SendUpdateToScoreboard(scope);
    }

    private async Task SendUpdateToScoreboard(IServiceScope scope)
    {
        var logger = scope.ServiceProvider.GetRequiredService<ILogger<ScoreboardManager>>();
        if (CurrentTournamentId is 0)
        {
            logger.LogDebug("Sending clear notification to scoreboard");
            await _scoreboardHub.Clients.All.ClearScoreboard();
            return;
        }

        var scoreboardDataCreator = scope.ServiceProvider.GetRequiredService<IScoreboardDataCreator>();
        var data = await scoreboardDataCreator.CreateScoreboardDataAsync(CurrentTournamentId);
        if (data is null)
            return;

        logger.LogDebug("Sending updated data to scoreboard");
        await _scoreboardHub.Clients.All.SendUpdate(data);
    }

    private async Task DatabaseUpdateListener(EntityChangedEvent changedEvent)
    {
        using var scope = _serviceProvider.CreateScope();
        var logger = scope.ServiceProvider.GetRequiredService<ILogger<ScoreboardManager>>();
        logger.LogDebug("Handling entity changed event: {}", changedEvent);

        switch (changedEvent.Entity)
        {
            case Tournament tournament:
                await HandleTournamentUpdate(tournament, changedEvent.Action, scope);
                return;

            case Club club:
                await HandleClubUpdate(club, changedEvent.Action, scope);
                return;

            case Discipline discipline:
                await HandleDisciplineUpdate(discipline, changedEvent.Action, scope);
                return;

            case Participant participant:
                await HandleParticipantUpdate(participant, changedEvent.Action, scope);
                return;

            case ParticipantResult result:
                await HandleParticipantResultUpdate(result, changedEvent.Action, scope);
                return;

            case Team team:
                await HandleTeamUpdate(team, changedEvent.Action, scope);
                return;

            case TeamDiscipline teamDiscipline:
                await HandleTeamDisciplineUpdate(teamDiscipline, changedEvent.Action, scope);
                return;
        }
    }

    private Task HandleTournamentUpdate(Tournament tournament, EntityAction action, IServiceScope scope)
    {
        if (tournament.Id != CurrentTournamentId)
            return Task.CompletedTask;

        switch (action)
        {
            case EntityAction.Added:
                return Task.CompletedTask;

            case EntityAction.Updated:
                return SendUpdateToScoreboard(scope);

            case EntityAction.Deleted:
                CurrentTournamentId = 0;
                return SendUpdateToScoreboard(scope);

            default:
                throw new ArgumentOutOfRangeException(nameof(action), action, null);
        }
    }

    private Task HandleClubUpdate(Club club, EntityAction action, IServiceScope scope)
    {
        if (club.TournamentId != CurrentTournamentId)
            return Task.CompletedTask;

        switch (action)
        {
            case EntityAction.Added:
                return Task.CompletedTask;

            case EntityAction.Updated:
            case EntityAction.Deleted:
                return SendUpdateToScoreboard(scope);

            default:
                throw new ArgumentOutOfRangeException(nameof(action), action, null);
        }
    }

    private Task HandleDisciplineUpdate(Discipline discipline, EntityAction action, IServiceScope scope)
    {
        if (discipline.TournamentId != CurrentTournamentId)
            return Task.CompletedTask;

        switch (action)
        {
            case EntityAction.Added:
            case EntityAction.Updated:
            case EntityAction.Deleted:
                return SendUpdateToScoreboard(scope);

            default:
                throw new ArgumentOutOfRangeException(nameof(action), action, null);
        }
    }

    private Task HandleParticipantUpdate(Participant participant, EntityAction action, IServiceScope scope)
    {
        if (participant.TournamentId != CurrentTournamentId)
            return Task.CompletedTask;

        switch (action)
        {
            case EntityAction.Added:
            case EntityAction.Updated:
            case EntityAction.Deleted:
                return SendUpdateToScoreboard(scope);

            default:
                throw new ArgumentOutOfRangeException(nameof(action), action, null);
        }
    }

    private async Task HandleParticipantResultUpdate(ParticipantResult result, EntityAction action, IServiceScope scope)
    {
        var dbContext = scope.ServiceProvider.GetRequiredService<ApplicationDbContext>();

        var isInCurrentTournament = await dbContext.Disciplines.AnyAsync(d =>
            d.Id == result.DisciplineId && d.TournamentId == CurrentTournamentId
        );
        if (!isInCurrentTournament)
            return;

        switch (action)
        {
            case EntityAction.Added:
            case EntityAction.Updated:
            case EntityAction.Deleted:
                await SendUpdateToScoreboard(scope);
                return;

            default:
                throw new ArgumentOutOfRangeException(nameof(action), action, null);
        }
    }

    private Task HandleTeamUpdate(Team team, EntityAction action, IServiceScope scope)
    {
        if (team.TournamentId != CurrentTournamentId)
            return Task.CompletedTask;

        switch (action)
        {
            case EntityAction.Added:
            case EntityAction.Updated:
            case EntityAction.Deleted:
                return SendUpdateToScoreboard(scope);

            default:
                throw new ArgumentOutOfRangeException(nameof(action), action, null);
        }
    }

    private Task HandleTeamDisciplineUpdate(TeamDiscipline teamDiscipline, EntityAction action, IServiceScope scope)
    {
        if (teamDiscipline.TournamentId != CurrentTournamentId)
            return Task.CompletedTask;

        switch (action)
        {
            case EntityAction.Added:
            case EntityAction.Updated:
            case EntityAction.Deleted:
                return SendUpdateToScoreboard(scope);

            default:
                throw new ArgumentOutOfRangeException(nameof(action), action, null);
        }
    }
}

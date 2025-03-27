using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Database.Notification;
using Turnierverwaltung.Server.Hubs;
using Turnierverwaltung.Server.Results.Scoreboard;

namespace Turnierverwaltung.Server.Utils;

public class ScoreboardManager : IDisposable
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

    public int CurrentTournamentId { get; private set; }

    public void Dispose()
    {
        _changeNotifier.UnregisterListener(DatabaseUpdateListener);
    }

    public async Task SetCurrentTournament(int tournamentId)
    {
        CurrentTournamentId = tournamentId;
        using var scope = _serviceProvider.CreateScope();
        await OnTournamentUpdate(scope);
    }

    private async Task OnTournamentUpdate(IServiceScope scope)
    {
        var scoreboardDataCreator = scope.ServiceProvider.GetRequiredService<IScoreboardDataCreator>();
        var data = await scoreboardDataCreator.CreateScoreboardDataAsync(CurrentTournamentId);
        if (data is null)
            return;

        await _scoreboardHub.Clients.All.SendUpdate(data);
    }

    private async Task DatabaseUpdateListener(EntityChangedEvent changedEvent)
    {
        using var scope = _serviceProvider.CreateScope();
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
                return OnTournamentUpdate(scope);

            case EntityAction.Deleted:
                CurrentTournamentId = 0;
                return Task.CompletedTask;

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
                return OnTournamentUpdate(scope);

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
                return OnTournamentUpdate(scope);

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
                return OnTournamentUpdate(scope);

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
                await OnTournamentUpdate(scope);
                return;

            default:
                throw new ArgumentOutOfRangeException(nameof(action), action, null);
        }
    }
}

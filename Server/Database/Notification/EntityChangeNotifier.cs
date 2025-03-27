namespace Turnierverwaltung.Server.Database.Notification;

public interface IEntityChangeNotifier
{
    void RegisterListener(Action<EntityChangedEvent> listener);
    void RegisterAsyncListener(Func<EntityChangedEvent, Task> listener);
    void UnregisterListener(Action<EntityChangedEvent> listener);
    void UnregisterListener(Func<EntityChangedEvent, Task> listener);
    Task NotifyAsync(EntityChangedEvent entityEvent);
}

public class EntityChangeNotifier : IEntityChangeNotifier
{
    private readonly List<Func<EntityChangedEvent, Task>> _asyncListeners = [];
    private readonly Lock _lock = new();
    private readonly List<Action<EntityChangedEvent>> _syncListeners = [];

    public void RegisterListener(Action<EntityChangedEvent> listener)
    {
        lock (_lock)
        {
            _syncListeners.Add(listener);
        }
    }

    public void RegisterAsyncListener(Func<EntityChangedEvent, Task> listener)
    {
        lock (_lock)
        {
            _asyncListeners.Add(listener);
        }
    }

    public void UnregisterListener(Action<EntityChangedEvent> listener)
    {
        lock (_lock)
        {
            _syncListeners.Remove(listener);
        }
    }

    public void UnregisterListener(Func<EntityChangedEvent, Task> listener)
    {
        lock (_lock)
        {
            _asyncListeners.Remove(listener);
        }
    }

    public Task NotifyAsync(EntityChangedEvent entityEvent)
    {
        foreach (var listener in _syncListeners)
            listener(entityEvent);

        return Task.WhenAll(_asyncListeners.Select(listener => listener(entityEvent)));
    }
}

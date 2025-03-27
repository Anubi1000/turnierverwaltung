using Microsoft.EntityFrameworkCore;

namespace Turnierverwaltung.Server.Database.Notification;

public record EntityChangedEvent(object Entity, EntityAction Action);

public static class EntityChangedEventUtils
{
    public static EntityAction ToAction(this EntityState state)
    {
        return state switch
        {
            EntityState.Added => EntityAction.Added,
            EntityState.Modified => EntityAction.Updated,
            EntityState.Deleted => EntityAction.Deleted,
            _ => throw new ArgumentOutOfRangeException(nameof(state), state, null),
        };
    }
}

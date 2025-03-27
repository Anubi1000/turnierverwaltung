using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Database.Notification;

namespace Turnierverwaltung.Server.Database;

public class ApplicationDbContext(DbContextOptions<ApplicationDbContext> options, IEntityChangeNotifier notifier)
    : DbContext(options)
{
    public DbSet<Club> Clubs { get; set; }
    public DbSet<Discipline> Disciplines { get; set; }
    public DbSet<Participant> Participants { get; set; }
    public DbSet<Team> Teams { get; set; }
    public DbSet<TeamDiscipline> TeamDisciplines { get; set; }
    public DbSet<Tournament> Tournaments { get; set; }
    public DbSet<ParticipantResult> ParticipantResults { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder
            .Entity<Tournament>()
            .Property(t => t.Date)
            .HasConversion(
                v => new DateTimeOffset(v).ToUnixTimeSeconds(),
                v => DateTimeOffset.FromUnixTimeSeconds(v).UtcDateTime
            );

        modelBuilder.Entity<Discipline>().OwnsMany(d => d.Values).ToJson();

        modelBuilder
            .Entity<Participant>()
            .HasMany(p => p.Results)
            .WithOne(pr => pr.Participant)
            .HasForeignKey(pr => pr.ParticipantId);

        modelBuilder
            .Entity<Discipline>()
            .HasMany(d => d.Results)
            .WithOne(pr => pr.Discipline)
            .HasForeignKey(pr => pr.DisciplineId);

        modelBuilder.Entity<ParticipantResult>().OwnsMany(pr => pr.Rounds).ToJson();
    }

    public override int SaveChanges(bool acceptAllChangesOnSuccess)
    {
        var ids = base.SaveChanges(acceptAllChangesOnSuccess);
        NotifyChanges().GetAwaiter().GetResult();
        return ids;
    }

    public override async Task<int> SaveChangesAsync(
        bool acceptAllChangesOnSuccess,
        CancellationToken cancellationToken = default
    )
    {
        var ids = await base.SaveChangesAsync(acceptAllChangesOnSuccess, cancellationToken);
        await NotifyChanges();
        return ids;
    }

    private async Task NotifyChanges()
    {
        var entries = ChangeTracker
            .Entries()
            .Where(e => e.State is EntityState.Added or EntityState.Modified or EntityState.Deleted);

        foreach (var entry in entries)
        {
            var eventData = new EntityChangedEvent(entry.Entity, entry.State.ToAction());
            await notifier.NotifyAsync(eventData);
        }
    }
}

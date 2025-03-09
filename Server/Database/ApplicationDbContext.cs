using System.Text.Json;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.ChangeTracking;
using Turnierverwaltung.Server.Database.Model;

namespace Turnierverwaltung.Server.Database;

public class ApplicationDbContext(DbContextOptions<ApplicationDbContext> options) : DbContext(options)
{
    public DbSet<Club> Clubs { get; set; }
    public DbSet<Discipline> Disciplines { get; set; }
    public DbSet<Participant> Participants { get; set; }
    public DbSet<Team> Teams { get; set; }
    public DbSet<TeamDiscipline> TeamDisciplines { get; set; }
    public DbSet<Tournament> Tournaments { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        var serializerOptions = new JsonSerializerOptions();

        modelBuilder
            .Entity<Tournament>()
            .Property(t => t.Date)
            .HasConversion(
                v => new DateTimeOffset(v).ToUnixTimeSeconds(),
                v => DateTimeOffset.FromUnixTimeSeconds(v).UtcDateTime
            );

        modelBuilder.Entity<Participant>().Property(p => p.Gender).HasConversion<string>();

        var participantResultComparer = new ValueComparer<Dictionary<int, Participant.DisciplineResult>>(
            (d1, d2) => d1 != null && d2 != null && d1.SequenceEqual(d2),
            d => d.Aggregate(0, (hash, kvp) => HashCode.Combine(hash, kvp.Key, kvp.Value.GetHashCode())),
            d => d.ToDictionary(entry => entry.Key, entry => entry.Value)
        );

        modelBuilder
            .Entity<Participant>()
            .Property(p => p.Results)
            .HasConversion(
                v => JsonSerializer.Serialize(v, serializerOptions),
                v => JsonSerializer.Deserialize<Dictionary<int, Participant.DisciplineResult>>(v, serializerOptions)!,
                participantResultComparer
            );

        modelBuilder
            .Entity<Discipline>()
            .Property(d => d.Values)
            .HasConversion(
                v => JsonSerializer.Serialize(v, serializerOptions),
                v => JsonSerializer.Deserialize<List<Discipline.Value>>(v, serializerOptions)!
            );
    }
}

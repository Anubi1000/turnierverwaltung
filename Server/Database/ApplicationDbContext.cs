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
}

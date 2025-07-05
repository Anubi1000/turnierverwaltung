using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database.Model;

namespace Turnierverwaltung.Server.Database;

public class ApplicationDbContext(DbContextOptions<ApplicationDbContext> options) : DbContext(options)
{
    public static readonly Func<ApplicationDbContext, int, Task<bool>> ExistsTournamentWithIdQuery =
        EF.CompileAsyncQuery(
            (ApplicationDbContext dbContext, int tournamentId) => dbContext.Tournaments.Any(t => t.Id == tournamentId)
        );

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
            .HasConversion(v => v.DayNumber, v => DateOnly.FromDayNumber(v));

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

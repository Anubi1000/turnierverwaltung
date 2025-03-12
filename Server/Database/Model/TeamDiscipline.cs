using System.ComponentModel.DataAnnotations;

namespace Turnierverwaltung.Server.Database.Model;

public class TeamDiscipline
{
    // Common properties
    public int Id { get; set; }

    [MaxLength(150)]
    public string Name { get; set; } = "";

    // Tournament
    public int TournamentId { get; init; }
    public Tournament Tournament { get; init; } = null!;

    // Based On (Disciplines)
    public ICollection<Discipline> BasedOn { get; init; } = new List<Discipline>();

    // ParticipatingTeams
    public ICollection<Team> ParticipatingTeams { get; set; } = new List<Team>();
}

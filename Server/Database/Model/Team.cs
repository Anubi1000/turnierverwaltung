using System.ComponentModel.DataAnnotations;

namespace Turnierverwaltung.Server.Database.Model;

public class Team
{
    public int Id { get; set; }

    [MaxLength(150)]
    public string Name { get; set; } = "";

    public int StartNumber { get; set; } = 0;
    public ICollection<Participant> Members { get; set; } = new List<Participant>();
    public ICollection<TeamDiscipline> ParticipatingDisciplines { get; set; } = new List<TeamDiscipline>();
    public int TournamentId { get; set; }
}

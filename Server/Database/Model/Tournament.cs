using System.ComponentModel.DataAnnotations;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Database.Model;

public class Tournament
{
    // Common properties
    public int Id { get; init; }

    [MaxLength(Constants.MaxNameLength)]
    public string Name { get; set; } = "";

    public DateTime Date { get; set; } = DateTime.UtcNow;
    public int TeamSize { get; init; } = 3;

    public ICollection<Club> Clubs { get; } = new List<Club>();
    public ICollection<Discipline> Disciplines { get; } = new List<Discipline>();
    public ICollection<Participant> Participants { get; } = new List<Participant>();
    public ICollection<Team> Teams { get; } = new List<Team>();
    public ICollection<TeamDiscipline> TeamDisciplines { get; } = new List<TeamDiscipline>();
}

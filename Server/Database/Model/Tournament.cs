using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Database.Model;

public class Tournament
{
    public int Id { get; init; }

    [MaxLength(150)]
    public string Name { get; set; } = "";

    [JsonConverter(typeof(DateTimeToTimestampConverter))]
    public DateTime Date { get; set; } = DateTime.UtcNow;

    public int TeamSize { get; init; } = 3;

    public ICollection<Club> Clubs { get; } = new List<Club>();
    public ICollection<Discipline> Disciplines { get; } = new List<Discipline>();
    public ICollection<Participant> Participants { get; } = new List<Participant>();
    public ICollection<Team> Teams { get; } = new List<Team>();
    public ICollection<TeamDiscipline> TeamDisciplines { get; } = new List<TeamDiscipline>();
}

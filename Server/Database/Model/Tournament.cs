namespace Turnierverwaltung.Server.Database.Model;

public sealed class Tournament
{
    // Common properties
    public int Id { get; init; }

    public string Name { get; set; } = "";

    public DateTime Date { get; set; } = DateTime.UtcNow;
    public int TeamSize { get; init; } = 3;
    public bool IsTeamSizeFixed { get; init; } = true;

    public ICollection<Club> Clubs { get; } = new List<Club>();
    public ICollection<Discipline> Disciplines { get; } = new List<Discipline>();
    public ICollection<Participant> Participants { get; } = new List<Participant>();
    public ICollection<Team> Teams { get; } = new List<Team>();
    public ICollection<TeamDiscipline> TeamDisciplines { get; } = new List<TeamDiscipline>();
}

using System.ComponentModel.DataAnnotations;

namespace Turnierverwaltung.Server.Database.Model;

public class TeamDiscipline
{
    public int Id { get; set; }

    [MaxLength(150)]
    public string Name { get; set; } = "";

    public ICollection<Discipline> BasedOn { get; init; } = new List<Discipline>();

    public int TournamentId { get; set; }
}

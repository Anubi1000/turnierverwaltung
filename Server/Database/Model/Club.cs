using System.ComponentModel.DataAnnotations;

namespace Turnierverwaltung.Server.Database.Model;

public class Club
{
    // Common properties
    public int Id { get; init; }

    [MaxLength(150)]
    public string Name { get; set; } = "";

    // Tournament
    public int TournamentId { get; init; }
    public Tournament Tournament { get; init; } = null!;

    // Members
    public ICollection<Participant> Members { get; } = new List<Participant>();
}

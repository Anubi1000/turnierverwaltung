using System.ComponentModel.DataAnnotations;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Database.Model;

public sealed class Club
{
    // Common properties
    public int Id { get; init; }

    public string Name { get; set; } = "";

    // Tournament
    public int TournamentId { get; init; }
    public Tournament Tournament { get; init; } = null!;

    // Members
    public ICollection<Participant> Members { get; } = new List<Participant>();
}

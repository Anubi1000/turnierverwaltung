using System.ComponentModel.DataAnnotations;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Database.Model;

public sealed class Team
{
    // Common properties
    public int Id { get; init; }

    [MaxLength(Constants.MaxNameLength)]
    public string Name { get; set; } = "";

    public int StartNumber { get; set; }

    // Tournament
    public int TournamentId { get; init; }
    public Tournament Tournament { get; init; } = null!;

    // Members
    public ICollection<Participant> Members { get; set; } = new List<Participant>();

    // ParticipatingDisciplines
    public ICollection<TeamDiscipline> ParticipatingDisciplines { get; set; } = new List<TeamDiscipline>();
}

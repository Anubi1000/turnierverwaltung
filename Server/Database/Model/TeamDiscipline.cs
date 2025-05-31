using System.ComponentModel.DataAnnotations;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Database.Model;

public sealed class TeamDiscipline
{
    // Common properties
    public int Id { get; set; }

    [MaxLength(Constants.MaxNameLength)]
    public string Name { get; set; } = "";

    public TeamScoreDisplayType DisplayType { get; set; } = TeamScoreDisplayType.Normal;

    // Tournament
    public int TournamentId { get; init; }
    public Tournament Tournament { get; init; } = null!;

    // Based On (Disciplines)
    public ICollection<Discipline> BasedOn { get; init; } = new List<Discipline>();

    // ParticipatingTeams
    public ICollection<Team> ParticipatingTeams { get; set; } = new List<Team>();
}

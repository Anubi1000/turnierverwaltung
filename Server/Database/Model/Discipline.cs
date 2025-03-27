using System.ComponentModel.DataAnnotations;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Database.Model;

public sealed class Discipline
{
    // Common properties
    public int Id { get; init; }

    [MaxLength(Constants.MaxNameLength)]
    public string Name { get; set; } = "";

    public int AmountOfBestRoundsToShow { get; set; } = 1;
    public bool AreGendersSeparated { get; set; }
    public List<Value> Values { get; init; } = [];

    // Tournament
    public int TournamentId { get; init; }
    public Tournament Tournament { get; init; } = null!;

    // Results
    public ICollection<ParticipantResult> Results { get; init; } = new List<ParticipantResult>();

    // Used In (Team Disciplines)
    public ICollection<TeamDiscipline> UsedIn { get; } = new List<TeamDiscipline>();

    public class Value
    {
        [MaxLength(Constants.MaxNameLength)]
        public string Name { get; set; } = "";

        public bool IsAdded { get; set; } = true;
    }
}

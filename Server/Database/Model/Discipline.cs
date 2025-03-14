using System.ComponentModel.DataAnnotations;

namespace Turnierverwaltung.Server.Database.Model;

public class Discipline
{
    // Common properties
    public int Id { get; init; }

    [MaxLength(150)]
    public string Name { get; set; } = "";

    public int AmountOfBestRoundsToShow { get; set; } = 1;
    public bool AreGendersSeparated { get; set; }

    // Need to explicitly mark as updated because property is saved as json
    public List<Value> Values { get; init; } = [];

    // Tournament
    public int TournamentId { get; init; }
    public Tournament Tournament { get; init; } = null!;

    // Results
    public ICollection<ParticipantResult> Results { get; init; } = null!;

    // Used In (Team Disciplines)
    public ICollection<TeamDiscipline> UsedIn { get; } = new List<TeamDiscipline>();

    public class Value
    {
        [MaxLength(150)]
        public string Name { get; set; } = "";

        public bool IsAdded { get; set; } = true;
    }
}

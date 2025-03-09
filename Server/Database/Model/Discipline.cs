using System.ComponentModel.DataAnnotations;

namespace Turnierverwaltung.Server.Database.Model;

public class Discipline
{
    public int Id { get; init; }

    public int AmountOfBestRoundsToShow { get; set; } = 1;

    public bool AreGendersSeparated { get; set; }

    [MaxLength(150)]
    public string Name { get; set; } = "";

    public int TournamentId { get; init; }

    // Need to explicitly mark as updated because property is saved as json
    public List<Value> Values { get; init; } = [];

    public ICollection<TeamDiscipline> UsedIn { get; } = new List<TeamDiscipline>();

    public class Value
    {
        [MaxLength(150)]
        public string Name { get; set; } = "";

        public bool IsAdded { get; set; } = true;
    }
}

namespace Turnierverwaltung.Server.Database.Model;

public class Participant
{
    public int Id { get; init; }

    public string Name { get; set; } = "";

    public int StartNumber { get; set; }
    public Gender Gender { get; set; } = Gender.Male;

    public int ClubId { get; set; }
    public Club Club { get; init; } = null!;

    // Need to explicitly mark as updated because property is saved as json
    public Dictionary<int, DisciplineResult> Results { get; init; } = new();

    public int TournamentId { get; init; }
    public Tournament Tournament { get; init; } = null!;

    public class DisciplineResult
    {
        public List<RoundResult> Rounds { get; set; } = [];
    }

    public class RoundResult
    {
        public List<double> Values { get; set; } = [];
    }
}

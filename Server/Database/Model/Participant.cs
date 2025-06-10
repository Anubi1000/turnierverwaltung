namespace Turnierverwaltung.Server.Database.Model;

public sealed class Participant
{
    // Common properties
    public int Id { get; init; }

    public string Name { get; set; } = "";

    public int StartNumber { get; set; }
    public Gender Gender { get; set; } = Gender.Male;

    // Tournament
    public int TournamentId { get; init; }
    public Tournament Tournament { get; init; } = null!;

    // Club
    public int ClubId { get; set; }
    public Club Club { get; init; } = null!;

    // Results
    public ICollection<ParticipantResult> Results { get; init; } = new List<ParticipantResult>();
}

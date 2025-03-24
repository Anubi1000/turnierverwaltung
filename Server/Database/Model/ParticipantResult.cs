using Microsoft.EntityFrameworkCore;

namespace Turnierverwaltung.Server.Database.Model;

[PrimaryKey(nameof(ParticipantId), nameof(DisciplineId))]
public class ParticipantResult
{
    // Participant
    public int ParticipantId { get; init; }
    public Participant Participant { get; init; } = null!;

    // Discipline
    public int DisciplineId { get; init; }
    public Discipline Discipline { get; init; } = null!;

    // Common properties
    public List<Round> Rounds { get; set; } = [];

    public class Round
    {
        public List<double> Values { get; set; } = [];
    }
}

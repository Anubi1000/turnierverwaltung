using Microsoft.EntityFrameworkCore;

namespace Turnierverwaltung.Server.Database.Model;

[PrimaryKey(nameof(ParticipantId), nameof(DisciplineId))]
public class ParticipantResult
{
    public int ParticipantId { get; init; }
    public Participant Participant { get; init; } = null!;

    public int DisciplineId { get; init; }
    public Discipline Discipline { get; init; } = null!;

    public List<Round> Rounds { get; set; } = [];

    public class Round
    {
        public List<double> Values { get; set; } = [];
    }
}

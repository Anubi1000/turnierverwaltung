namespace Turnierverwaltung.Server.Model.Transfer.Participant;

public record ParticipantResultDetailDto(
    List<ParticipantResultDetailDto.DisciplineValue> DisciplineValues,
    List<ParticipantResultDetailDto.RoundResult> Rounds
)
{
    public record DisciplineValue(string Name, bool IsAdded);

    public record RoundResult(List<double> Values);
}

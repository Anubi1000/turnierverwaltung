namespace Turnierverwaltung.Server.Model.Transfer.Participant;

public record ParticipantResultEditDto(List<ParticipantResultEditDto.RoundResult> Rounds)
{
    public record RoundResult(List<double> Values);
}

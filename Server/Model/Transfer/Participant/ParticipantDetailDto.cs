using Turnierverwaltung.Server.Database.Model;

namespace Turnierverwaltung.Server.Model.Transfer.Participant;

public record ParticipantDetailDto(
    int Id,
    string Name,
    int StartNumber,
    Gender Gender,
    int ClubId,
    string ClubName,
    List<ParticipantDetailDto.Discipline> Disciplines
)
{
    public record Discipline(int Id, string Name);
}

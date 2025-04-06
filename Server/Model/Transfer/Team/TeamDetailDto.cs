namespace Turnierverwaltung.Server.Model.Transfer.Team;

public record TeamDetailDto(
    string Name,
    int StartNumber,
    int MemberCount,
    List<TeamDetailDto.TeamDiscipline> ParticipatingDisciplines
)
{
    public record TeamDiscipline(int Id, string Name);
}

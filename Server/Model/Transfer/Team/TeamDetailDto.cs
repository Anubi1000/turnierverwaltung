namespace Turnierverwaltung.Server.Model.Transfer.Team;

public record TeamDetailDto(
    string Name,
    int StartNumber,
    List<TeamDetailDto.Member> Members,
    List<TeamDetailDto.TeamDiscipline> ParticipatingDisciplines
)
{
    public record Member(int Id, string Name);
    public record TeamDiscipline(int Id, string Name);
}

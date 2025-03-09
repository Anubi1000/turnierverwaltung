namespace Turnierverwaltung.Server.Model.Transfer.TeamDiscipline;

public record TeamDisciplineDetailDto(string Name, List<TeamDisciplineDetailDto.Discipline> BasedOn)
{
    public record Discipline(int Id, string Name);
}

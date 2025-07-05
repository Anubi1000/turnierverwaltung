using Turnierverwaltung.Server.Database.Model;

namespace Turnierverwaltung.Server.Model.Transfer.TeamDiscipline;

public record TeamDisciplineDetailDto(
    string Name,
    TeamScoreDisplayType DisplayType,
    List<TeamDisciplineDetailDto.Discipline> BasedOn
)
{
    public record Discipline(int Id, string Name);
}

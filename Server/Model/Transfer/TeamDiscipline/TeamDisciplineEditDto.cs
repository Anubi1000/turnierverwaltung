using Turnierverwaltung.Server.Database.Model;

namespace Turnierverwaltung.Server.Model.Transfer.TeamDiscipline;

public record TeamDisciplineEditDto(string Name, TeamScoreDisplayType DisplayType, List<int> BasedOn);

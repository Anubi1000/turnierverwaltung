namespace Turnierverwaltung.Server.Model.Transfer.Team;

public record TeamEditDto(string Name, int StartNumber, List<int> Members, List<int> ParticipatingDisciplines);

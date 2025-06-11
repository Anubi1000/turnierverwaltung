namespace Turnierverwaltung.Server.Model.Transfer.Tournament;

public record TournamentEditDto(string Name, DateOnly Date, int TeamSize, bool IsTeamSizeFixed);

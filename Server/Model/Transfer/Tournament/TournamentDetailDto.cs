namespace Turnierverwaltung.Server.Model.Transfer.Tournament;

public record TournamentDetailDto(
    int Id,
    string Name,
    DateTime Date,
    int TeamSize,
    bool IsTeamSizeFixed,
    int ClubCount,
    int DisciplineCount,
    int ParticipantCount,
    int TeamCount
);

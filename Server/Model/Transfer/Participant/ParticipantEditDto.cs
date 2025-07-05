using Turnierverwaltung.Server.Database.Model;

namespace Turnierverwaltung.Server.Model.Transfer.Participant;

public record ParticipantEditDto(string Name, int StartNumber, Gender Gender, int ClubId);

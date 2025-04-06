using System.Text.Json.Serialization;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Model.Transfer.Tournament;

public record TournamentEditDto(
    string Name,
    [property: JsonConverter(typeof(DateTimeToTimestampConverter))] DateTime Date,
    int TeamSize,
    bool IsTeamSizeFixed
);

using System.Text.Json.Serialization;
using Turnierverwaltung.Server.Model.Transfer;
using Turnierverwaltung.Server.Model.Transfer.Club;
using Turnierverwaltung.Server.Model.Transfer.Discipline;
using Turnierverwaltung.Server.Model.Transfer.Participant;
using Turnierverwaltung.Server.Model.Transfer.Team;
using Turnierverwaltung.Server.Model.Transfer.TeamDiscipline;
using Turnierverwaltung.Server.Model.Transfer.Tournament;
using Turnierverwaltung.Server.Results.Scoreboard;

namespace Turnierverwaltung.Server.Utils;

// Club
[JsonSerializable(typeof(ClubDetailDto))]
[JsonSerializable(typeof(ClubEditDto), GenerationMode = JsonSourceGenerationMode.Metadata)]
[JsonSerializable(typeof(List<ListClubDto>))]
// Discipline
[JsonSerializable(typeof(DisciplineDetailDto))]
[JsonSerializable(typeof(List<DisciplineDetailDto.Value>), TypeInfoPropertyName = "DisciplineDetailDtoListValue")]
[JsonSerializable(typeof(DisciplineDetailDto.Value), TypeInfoPropertyName = "DisciplineDetailDtoValue")]
[JsonSerializable(typeof(DisciplineEditDto), GenerationMode = JsonSourceGenerationMode.Metadata)]
[JsonSerializable(typeof(List<ListDisciplineDto>))]
// Participant
[JsonSerializable(typeof(List<ListParticipantDto>))]
[JsonSerializable(typeof(List<ParticipantDetailDto>))]
[JsonSerializable(typeof(ParticipantEditDto), GenerationMode = JsonSourceGenerationMode.Metadata)]
[JsonSerializable(typeof(ParticipantResultDetailDto))]
[JsonSerializable(
    typeof(List<ParticipantResultDetailDto.RoundResult>),
    TypeInfoPropertyName = "ParticipantResultDetailDtoListRoundResult"
)]
[JsonSerializable(
    typeof(ParticipantResultDetailDto.RoundResult),
    TypeInfoPropertyName = "ParticipantResultDetailDtoRoundResult"
)]
[JsonSerializable(typeof(ParticipantResultEditDto), GenerationMode = JsonSourceGenerationMode.Metadata)]
// Team
[JsonSerializable(typeof(List<ListTeamDto>))]
[JsonSerializable(typeof(TeamDetailDto))]
[JsonSerializable(typeof(TeamEditDto), GenerationMode = JsonSourceGenerationMode.Metadata)]
// TeamDiscipline
[JsonSerializable(typeof(List<ListTeamDisciplineDto>))]
[JsonSerializable(typeof(TeamDisciplineDetailDto))]
[JsonSerializable(
    typeof(List<TeamDisciplineDetailDto.Discipline>),
    TypeInfoPropertyName = "TeamDisciplineDetailDtoListDiscipline"
)]
[JsonSerializable(
    typeof(TeamDisciplineDetailDto.Discipline),
    TypeInfoPropertyName = "TeamDisciplineDetailDtoDiscipline"
)]
[JsonSerializable(typeof(TeamDisciplineEditDto), GenerationMode = JsonSourceGenerationMode.Metadata)]
// Tournament
[JsonSerializable(typeof(List<ListTournamentDto>))]
[JsonSerializable(typeof(TournamentDetailDto))]
[JsonSerializable(typeof(TournamentEditDto), GenerationMode = JsonSourceGenerationMode.Metadata)]
// Scoreboard
[JsonSerializable(typeof(ScoreboardData))]
// Util
[JsonSerializable(typeof(AuthInfoDto))]
[JsonSerializable(typeof(WordDocGenerationDto))]
// Other
[JsonSerializable(typeof(HttpValidationProblemDetails))]
public partial class AppJsonSerializerContext : JsonSerializerContext;

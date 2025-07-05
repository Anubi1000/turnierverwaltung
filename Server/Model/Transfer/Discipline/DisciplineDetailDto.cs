namespace Turnierverwaltung.Server.Model.Transfer.Discipline;

public record DisciplineDetailDto(
    int Id,
    int TournamentId,
    string Name,
    int AmountOfBestRoundsToShow,
    bool AreGendersSeparated,
    bool ShowInResults,
    List<DisciplineDetailDto.Value> Values
)
{
    public record Value(string Name, bool IsAdded);
}

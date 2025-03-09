namespace Turnierverwaltung.Server.Model.Transfer.Discipline;

public record DisciplineEditDto(
    string Name,
    int AmountOfBestRoundsToShow,
    bool AreGendersSeparated,
    List<DisciplineEditDto.Value> Values
)
{
    public record Value(string Name, bool IsAdded);
}

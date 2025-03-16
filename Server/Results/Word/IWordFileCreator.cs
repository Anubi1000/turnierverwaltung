using Turnierverwaltung.Server.Results.Scoreboard;

namespace Turnierverwaltung.Server.Results.Word;

public interface IWordFileCreator
{
    public MemoryStream CreateWordFileAsStream(ScoreboardData scoreboardData);
    public MemoryStream CreateWordFileForTableAsStream(ScoreboardData scoreboardData, int tableIndex);
}

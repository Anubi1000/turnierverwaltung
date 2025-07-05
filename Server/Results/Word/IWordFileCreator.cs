using Turnierverwaltung.Server.Results.Scoreboard;

namespace Turnierverwaltung.Server.Results.Word;

public interface IWordFileCreator
{
    public Task<MemoryStream> CreateWordFileAsStream(ScoreboardData scoreboardData);
    public Task<MemoryStream> CreateWordFileForTableAsStream(ScoreboardData scoreboardData, int tableIndex);
}

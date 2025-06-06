namespace Turnierverwaltung.Server.Utils;

public interface IUserDataService
{
    public string DataDirectory { get; }
    public string GetUserDataPath(UserDataType type);
    public Task<ReadOnlyMemory<byte>?> GetWordDocumentLogo();
}

public class UserDataService : IUserDataService
{
    private readonly SemaphoreSlim _syncLock = new(1, 1);
    private bool _searchedWordDocumentLogo;
    private ReadOnlyMemory<byte>? _wordDocumentLogo;

    private UserDataService(string dataDirectory)
    {
        DataDirectory = dataDirectory;
    }

    public string DataDirectory { get; }

    public string GetUserDataPath(UserDataType type)
    {
        return type switch
        {
            UserDataType.Database => Path.Combine(DataDirectory, "Data.db"),
            UserDataType.Config => Path.Combine(DataDirectory, "Config.json"),
            UserDataType.WordDocumentLogo => Path.Combine(DataDirectory, "WordDocumentLogo.png"),
            _ => throw new ArgumentOutOfRangeException(nameof(type), type, null),
        };
    }

    public async Task<ReadOnlyMemory<byte>?> GetWordDocumentLogo()
    {
        if (_searchedWordDocumentLogo)
            return _wordDocumentLogo;

        await _syncLock.WaitAsync();
        try
        {
            if (_searchedWordDocumentLogo)
                return _wordDocumentLogo;

            var path = GetUserDataPath(UserDataType.WordDocumentLogo);
            if (!File.Exists(path))
            {
                _searchedWordDocumentLogo = true;
                return null;
            }

            var bytes = await File.ReadAllBytesAsync(path);
            _wordDocumentLogo = bytes;
            _searchedWordDocumentLogo = true;
            return _wordDocumentLogo;
        }
        finally
        {
            _syncLock.Release();
        }
    }

    public static UserDataService CreateNew()
    {
        var dir = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
        if (dir == null)
            throw new DirectoryNotFoundException("Could not find suitable data dir");

        dir = Path.Combine(dir, "Turnierverwaltung");
        Directory.CreateDirectory(dir);

        return new UserDataService(dir);
    }
}

public enum UserDataType
{
    Database,
    Config,
    WordDocumentLogo,
}

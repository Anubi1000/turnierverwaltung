namespace Turnierverwaltung.Server.Utils;

public interface IUserDataService
{
    public string DataDirectory { get; }
    public string GetUserDataPath(UserDataType type);
    public byte[]? GetWordDocumentLogo();
}

public class UserDataService : IUserDataService
{
    private readonly Lock _syncLock = new();
    private bool _searchedWordDocumentLogo;

    private byte[]? _wordDocumentLogo;

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
            UserDataType.CertificateWithKey => Path.Combine(DataDirectory, "Certificate.pfx"),
            UserDataType.Certificate => Path.Combine(DataDirectory, "Certificate.cer"),
            UserDataType.WordDocumentLogo => Path.Combine(DataDirectory, "WordDocumentLogo.png"),
            _ => throw new ArgumentOutOfRangeException(nameof(type), type, null),
        };
    }

    public byte[]? GetWordDocumentLogo()
    {
        if (_searchedWordDocumentLogo)
            return _wordDocumentLogo;

        lock (_syncLock)
        {
            if (_searchedWordDocumentLogo)
                return _wordDocumentLogo;

            var path = GetUserDataPath(UserDataType.WordDocumentLogo);
            if (!File.Exists(path))
            {
                _searchedWordDocumentLogo = true;
                return null;
            }

            var bytes = File.ReadAllBytes(path);
            _wordDocumentLogo = bytes;
            return bytes;
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
    CertificateWithKey,
    Certificate,
    WordDocumentLogo,
}

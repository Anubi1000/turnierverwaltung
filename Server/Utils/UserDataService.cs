using System.Text.Json;
using Turnierverwaltung.Server.Config;

namespace Turnierverwaltung.Server.Utils;

public class UserDataService
{
    public const string WordIconPath = "WordIcon.png";

    private readonly Dictionary<string, byte[]?> _fileCache = new();

    private UserDataService(string dataDirectory)
    {
        DataDirectory = dataDirectory;
    }

    public string DataDirectory { get; }

    private string GetConfigFilePath()
    {
        return Path.Combine(DataDirectory, "Config.json");
    }

    public string GetDatabasePath()
    {
        return Path.Combine(DataDirectory, "Data.db");
    }

    public async Task<byte[]?> ReadAsset(string name)
    {
        if (_fileCache.TryGetValue(name, out var cachedBytes))
            return cachedBytes;

        var targetPath = Path.Combine(DataDirectory, name);
        if (!File.Exists(targetPath))
        {
            _fileCache[name] = null;
            return null;
        }

        try
        {
            var bytes = await File.ReadAllBytesAsync(targetPath);
            _fileCache[name] = bytes;
            return bytes;
        }
        catch
        {
            _fileCache[name] = null;
            return null;
        }
    }

    public AppConfig LoadConfig()
    {
        var configPath = GetConfigFilePath();

        AppConfig config;
        if (File.Exists(configPath))
            try
            {
                var content = File.ReadAllText(configPath);
                config = JsonSerializer.Deserialize(content, AppConfigJsonContext.Default.AppConfig) ?? new AppConfig();
            }
            catch
            {
                config = new AppConfig();
            }
        else
            config = new AppConfig();

        var changed = false;

        if (config.ClubName is null)
        {
            config.ClubName = "Ein Verein";
            changed = true;
        }

        if (changed || !File.Exists(configPath))
        {
            var json = JsonSerializer.Serialize(config, AppConfigJsonContext.Default.AppConfig);
            File.WriteAllText(configPath, json);
        }

        return config;
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

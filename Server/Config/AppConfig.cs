using System.Text.Json;
using System.Text.Json.Serialization;

namespace Turnierverwaltung.Server.Config;

public class AppConfig
{
    public required string ClubName { get; set; }

    public static void SetupFile(string path)
    {
        if (File.Exists(path))
            return;

        var defaultConfig = new AppConfig { ClubName = "Ein Verein" };

        var json = JsonSerializer.Serialize(
            new AppConfigFile { AppSettings = defaultConfig },
            AppConfigJsonContext.Default.AppConfigFile
        );

        File.WriteAllText(path, json);
    }
}

public class AppConfigFile
{
    public required AppConfig AppSettings { get; set; }
}

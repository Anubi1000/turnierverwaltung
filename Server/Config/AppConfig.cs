using System.Text.Json;

namespace Turnierverwaltung.Server.Config;

public class AppConfig
{
    /// <summary>
    /// The name of the club that is shown in the exported word documents.
    /// </summary>
    public string ClubName { get; set; } = "Ein Verein";

    /// <summary>
    /// The path to the logo used in the exported word documents.
    /// Is relative to the config directory and needs to be a png.
    /// Icon is not added when path is empty or file is not valid.
    /// </summary>
    public string WordLogoPath { get; set; } = "WordIcon.png";

    public static void SetupFile(string path)
    {
        var defaultConfig = new AppConfig();

        AppConfigFile? configFile = null;

        if (File.Exists(path))
        {
            configFile = JsonSerializer.Deserialize(File.ReadAllText(path), AppConfigJsonContext.Default.AppConfigFile);
        }

        if (configFile is null)
        {
            configFile = new AppConfigFile
            {
                AppSettings = defaultConfig
            };
        }
        else
        {
            if (configFile.AppSettings is null)
            {
                configFile.AppSettings = defaultConfig;
            }
            else
            {
                configFile.AppSettings.ClubName ??= defaultConfig.ClubName;
                configFile.AppSettings.WordLogoPath ??= defaultConfig.WordLogoPath;
            }
        }

        var json = JsonSerializer.Serialize(configFile, AppConfigJsonContext.Default.AppConfigFile);
        File.WriteAllText(path, json);
    }
}

public class AppConfigFile
{
    public required AppConfig AppSettings { get; set; }
}

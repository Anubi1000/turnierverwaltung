using System.Text.Json.Serialization;

namespace Turnierverwaltung.Server.Config;

[JsonSourceGenerationOptions(WriteIndented = true)]
[JsonSerializable(typeof(AppConfig))]
[JsonSerializable(typeof(AppConfigFile))]
public partial class AppConfigJsonContext : JsonSerializerContext;

using System.Text.Json;
using System.Text.Json.Serialization;

namespace Turnierverwaltung.Server.Utils;

public class DateTimeToTimestampConverter : JsonConverter<DateTime>
{
    public override DateTime Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
    {
        var unixTimestamp = reader.GetInt64();
        return DateTimeOffset.FromUnixTimeMilliseconds(unixTimestamp).UtcDateTime;
    }

    public override void Write(Utf8JsonWriter writer, DateTime value, JsonSerializerOptions options)
    {
        var unixTimestamp = new DateTimeOffset(value).ToUnixTimeMilliseconds();
        writer.WriteNumberValue(unixTimestamp);
    }
}

using System.Text.Json;
using System.Text.Json.Serialization;

namespace Turnierverwaltung.Server.Utils;

public class DateOnlyToTimestampConverter : JsonConverter<DateOnly>
{
    public override DateOnly Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
    {
        var unixTimestamp = reader.GetInt64();
        var dateTime = DateTimeOffset.FromUnixTimeMilliseconds(unixTimestamp).UtcDateTime;
        return DateOnly.FromDateTime(dateTime);
    }

    public override void Write(Utf8JsonWriter writer, DateOnly value, JsonSerializerOptions options)
    {
        var dateTime = value.ToDateTime(TimeOnly.MinValue, DateTimeKind.Utc);
        var unixTimestamp = new DateTimeOffset(dateTime).ToUnixTimeMilliseconds();
        writer.WriteNumberValue(unixTimestamp);
    }
}

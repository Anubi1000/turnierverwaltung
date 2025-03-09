using System.Text.Json;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Tests.Utils;

public class DateTimeToTimestampConverterTests
{
    private readonly JsonSerializerOptions _options = new() { Converters = { new DateTimeToTimestampConverter() } };

    [Fact]
    public void CanSerializeDateTime_ToUnixTimestamp()
    {
        var dateTime = new DateTime(2023, 1, 1, 0, 0, 0, DateTimeKind.Utc);
        var json = JsonSerializer.Serialize(dateTime, _options);

        Assert.Equal("1672531200000", json); // Unix timestamp in milliseconds
    }

    [Fact]
    public void CanDeserializeUnixTimestamp_ToDateTime()
    {
        const string json = "1672531200000";
        var dateTime = JsonSerializer.Deserialize<DateTime>(json, _options);

        Assert.Equal(new DateTime(2023, 1, 1, 0, 0, 0, DateTimeKind.Utc), dateTime);
    }
}

using System.Text.Json;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Tests.Utils;

public class DateOnlyToTimestampConverterTests
{
    private readonly JsonSerializerOptions _options = new() { Converters = { new DateOnlyToTimestampConverter() } };

    [Fact]
    public void CanSerializeDateTime_ToUnixTimestamp()
    {
        var dateOnly = new DateOnly(2023, 1, 1);
        var json = JsonSerializer.Serialize(dateOnly, _options);

        Assert.Equal("1672531200000", json); // Unix timestamp in milliseconds
    }

    [Fact]
    public void CanDeserializeUnixTimestamp_ToDateTime()
    {
        const string json = "1672531200000";
        var dateOnly = JsonSerializer.Deserialize<DateOnly>(json, _options);

        Assert.Equal(new DateOnly(2023, 1, 1), dateOnly);
    }
}

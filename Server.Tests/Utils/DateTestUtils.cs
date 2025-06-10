namespace Turnierverwaltung.Server.Tests.Utils;

public static class DateTestUtils
{
    public static DateTime GetTestDate(int daysAdded = 0)
    {
        return new DateTime(2020, 1, 1, 0, 0, 0, DateTimeKind.Utc).AddDays(daysAdded);
    }
}

namespace Turnierverwaltung.Server.Tests.Utils;

public static class DateTestUtils
{
    public static DateOnly GetTestDate(int daysAdded = 0)
    {
        return DateOnly.FromDayNumber(daysAdded + 1000);
    }
}

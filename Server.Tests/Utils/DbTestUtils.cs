using Microsoft.Data.Sqlite;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Notification;

namespace Turnierverwaltung.Server.Tests.Utils;

public static class DbTestUtils
{
    public static ApplicationDbContext GetInMemoryApplicationDbContext()
    {
        var connection = new SqliteConnection("DataSource=:memory:");
        connection.Open();

        var options = new DbContextOptionsBuilder<ApplicationDbContext>().UseSqlite(connection).Options;

        var context = new ApplicationDbContext(options, new EntityChangeNotifier());
        context.Database.EnsureCreated();

        return context;
    }
}

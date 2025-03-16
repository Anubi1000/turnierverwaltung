using FluentValidation;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Endpoints;

namespace Turnierverwaltung.Server;

public static partial class Program
{
    public static void Main(string[] args)
    {
        ValidatorOptions.Global.LanguageManager.Enabled = false;

        var builder = WebApplication.CreateBuilder(args);
        ConfigureServices(builder.Services, builder);

        var app = builder.Build();

        using (var scope = app.Services.CreateScope())
        {
            var dbContext = scope.ServiceProvider.GetRequiredService<ApplicationDbContext>();
            dbContext.Database.Migrate();
        }

        if (app.Environment.IsDevelopment())
        {
            app.MapOpenApi();
            app.UseDeveloperExceptionPage();
        }

        app.UseAuthentication();
        app.UseAuthorization();

        app.MapGroup("/").WithTags("Util").MapUtilEndpoints();

        app.MapTournamentEndpoints();
        app.MapOverviewEndpoints();
        app.MapClubEndpoints();
        app.MapDisciplineEndpoints();
        app.MapParticipantEndpoints();
        app.MapTeamDisciplineEndpoints();

#if DEBUG
        app.MapReverseProxy();
#endif

        app.UseDefaultFiles();
        app.MapStaticAssets();
        app.MapFallbackToFile("index.html");

        app.Run();
    }

    private static string GetDataDirectory()
    {
        var dir = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
        if (dir == "")
            dir = AppContext.BaseDirectory;
        if (dir == null)
            throw new DirectoryNotFoundException("Could not find suitable data dir");

        dir += "/Turnierverwaltung";

        Directory.CreateDirectory(dir);

        return dir;
    }

    public static string GetUserData(UserDataType type)
    {
        var dir = GetDataDirectory();

        dir += type switch
        {
            UserDataType.Database => "/Data.db",
            UserDataType.WordDocumentIcon => "/Custom/WordIcon.png",
            _ => throw new ArgumentOutOfRangeException(nameof(type), type, null),
        };

        return dir;
    }
}

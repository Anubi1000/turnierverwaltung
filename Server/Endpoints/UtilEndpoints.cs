using Microsoft.AspNetCore.Http.HttpResults;
using Turnierverwaltung.Server.Model.Transfer;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Endpoints;

public static class UtilEndpoints
{
    public static IEndpointRouteBuilder MapUtilEndpoints(this IEndpointRouteBuilder builder)
    {
        var group = builder.MapGroup("/api").WithTags("Util");

        group.MapGet("/ping", Ping).WithName("GetPing");
        group.MapGet("/auth", CheckAuth).WithName("CheckAuth");

        group.MapGet("/files/scoreboard_icon.png", GetScoreboardIcon).WithName("GetScoreboardIcon");

        return builder;
    }

    public static string Ping()
    {
        return "pong";
    }

    public static AuthInfoDto CheckAuth(HttpContext httpContext)
    {
        var isAuthenticated = httpContext.User.Identity is { IsAuthenticated: true };
        return new AuthInfoDto(isAuthenticated);
    }

    public static async Task<Results<NotFound, FileContentHttpResult>> GetScoreboardIcon(UserDataService dataService)
    {
        var data = await dataService.ReadAsset(UserDataService.ScoreboardIconPath);
        return data is null ? TypedResults.NotFound() : TypedResults.File(data, MimeTypes.Png);
    }
}

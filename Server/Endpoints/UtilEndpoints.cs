using Turnierverwaltung.Server.Model.Transfer;

namespace Turnierverwaltung.Server.Endpoints;

public static class UtilEndpoints
{
    public static IEndpointRouteBuilder MapUtilEndpoints(this IEndpointRouteBuilder builder)
    {
        builder.MapGet("/ping", Ping).WithName("GetPing");
        builder.MapGet("/auth", CheckAuth).WithName("CheckAuth");

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
}

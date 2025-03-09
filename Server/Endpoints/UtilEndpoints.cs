using Turnierverwaltung.Server.Model.Transfer;

namespace Turnierverwaltung.Server.Endpoints;

public static class UtilEndpoints
{
    public static IEndpointRouteBuilder MapUtilEndpoints(this IEndpointRouteBuilder builder)
    {
        builder.MapGet("/ping", Ping);
        builder.MapGet("/auth", CheckAuth);

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

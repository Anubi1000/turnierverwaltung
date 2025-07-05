using System.Net;
using System.Security.Claims;
using System.Text.Encodings.Web;
using Microsoft.AspNetCore.Authentication;
using Microsoft.Extensions.Options;

namespace Turnierverwaltung.Server.Auth;

public class LocalhostAuthenticationHandler : AuthenticationHandler<AuthenticationSchemeOptions>
{
    private readonly AuthenticationTicket _localhostTicket;

    public LocalhostAuthenticationHandler(
        IOptionsMonitor<AuthenticationSchemeOptions> options,
        ILoggerFactory logger,
        UrlEncoder encoder
    )
        : base(options, logger, encoder)
    {
        var claims = new[] { new Claim(ClaimTypes.Name, "localhost_user") };
        var identity = new ClaimsIdentity(claims, "localhost");
        var principal = new ClaimsPrincipal(identity);
        _localhostTicket = new AuthenticationTicket(principal, "localhost");
    }

    protected override Task<AuthenticateResult> HandleAuthenticateAsync()
    {
        if (Context.Connection.RemoteIpAddress is not null && IPAddress.IsLoopback(Context.Connection.RemoteIpAddress))
            return Task.FromResult(AuthenticateResult.Success(_localhostTicket));

        return Task.FromResult(AuthenticateResult.Fail("Not from localhost"));
    }
}

using System.Net;
using System.Security.Claims;
using FluentAssertions;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using Microsoft.Extensions.WebEncoders.Testing;
using NSubstitute;
using Turnierverwaltung.Server.Auth;

namespace Turnierverwaltung.Server.Tests.Auth;

public class LocalhostAuthenticationHandlerTests
{
    private static async Task<LocalhostAuthenticationHandler> CreateHandler(IPAddress? remoteIpAddress)
    {
        var optionsMonitor = Substitute.For<IOptionsMonitor<AuthenticationSchemeOptions>>();
        optionsMonitor.Get("localhost").Returns(new AuthenticationSchemeOptions());

        var loggerFactory = new LoggerFactory();
        var urlEncoder = new UrlTestEncoder();

        var httpContext = new DefaultHttpContext { Connection = { RemoteIpAddress = remoteIpAddress } };
        var scheme = new AuthenticationScheme("localhost", null, typeof(LocalhostAuthenticationHandler));

        var handler = new LocalhostAuthenticationHandler(optionsMonitor, loggerFactory, urlEncoder);
        await handler.InitializeAsync(scheme, httpContext);

        return handler;
    }

    [Theory]
    [InlineData("127.0.0.1")]
    [InlineData("::1")]
    public async Task WhenRequestIsFromLocalhost_ShouldSucceed(string remoteIpAddress)
    {
        var handler = await CreateHandler(IPAddress.Parse(remoteIpAddress));
        var result = await handler.AuthenticateAsync();

        result.Succeeded.Should().BeTrue();
        result.Principal.Should().NotBeNull();
        result.Principal.Claims.Should().Contain(c => c.Type == ClaimTypes.Name && c.Value == "localhost_user");
    }

    [Theory]
    [InlineData(null)]
    [InlineData("192.168.1.1")]
    [InlineData("1.2.3.4")]
    public async Task WhenRequestIsNotFromLocalhost_ShouldFail(string? remoteIpAddress)
    {
        var address = remoteIpAddress is not null ? IPAddress.Parse("192.168.1.1") : null;
        var handler = await CreateHandler(address);
        var result = await handler.AuthenticateAsync();

        result.Succeeded.Should().BeFalse();
        result.Failure?.Message.Should().Be("Not from localhost");
    }
}

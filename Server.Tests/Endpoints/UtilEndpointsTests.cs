using System.Security.Claims;
using AwesomeAssertions;
using Microsoft.AspNetCore.Http;
using NSubstitute;
using Turnierverwaltung.Server.Endpoints;

namespace Turnierverwaltung.Server.Tests.Endpoints;

public class UtilEndpointsTests
{
    [Fact]
    public void Ping_ReturnsPong()
    {
        var result = UtilEndpoints.Ping();
        result.Should().Be("pong");
    }

    [Theory]
    [InlineData(true)]
    [InlineData(false)]
    public void CheckAuth_ReturnsExpectedResult(bool isAuthenticated)
    {
        var identity = Substitute.For<ClaimsIdentity>();
        identity.IsAuthenticated.Returns(isAuthenticated);

        var httpContext = new DefaultHttpContext { User = new ClaimsPrincipal(identity) };

        var result = UtilEndpoints.CheckAuth(httpContext);

        result.IsAuthenticated.Should().Be(isAuthenticated);
    }

    [Fact]
    public void WhenUserHasNoIdentity_ReturnsFalse()
    {
        var httpContext = new DefaultHttpContext { User = new ClaimsPrincipal() };

        var result = UtilEndpoints.CheckAuth(httpContext);

        result.Should().NotBeNull();
        result.IsAuthenticated.Should().BeFalse();
    }
}

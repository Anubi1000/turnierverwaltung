using System.Diagnostics.CodeAnalysis;
using AwesomeAssertions;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Tests.Utils;

namespace Turnierverwaltung.Server.Tests.Endpoints.Clubs;

public class DeleteClubTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();

    [SuppressMessage("Usage", "CA1816")]
    public void Dispose()
    {
        _dbContext.Database.GetDbConnection().Dispose();
        _dbContext.Dispose();
    }

    [Fact]
    public async Task WhenClubExists_ReturnsOk()
    {
        var tournament = new Tournament
        {
            Id = 1,
            Clubs =
            {
                new Club { Id = 1, Name = "Test Club" },
            },
        };

        _dbContext.Tournaments.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var result = await ClubEndpoints.DeleteClub(_dbContext, 1);
        result.Should().BeResult<Results<NotFound, Ok>, Ok>();

        var club = await _dbContext.Clubs.SingleOrDefaultAsync(c => c.Id == 1, TestContext.Current.CancellationToken);
        club.Should().BeNull();
    }

    [Fact]
    public async Task WhenClubDoesNotExist_ReturnsNotFound()
    {
        var result = await ClubEndpoints.DeleteClub(_dbContext, 1);
        result.Should().BeResult<Results<NotFound, Ok>, NotFound>();
    }
}

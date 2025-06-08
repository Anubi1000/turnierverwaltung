using System.Diagnostics.CodeAnalysis;
using AwesomeAssertions;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Model.Transfer.Club;
using Turnierverwaltung.Server.Tests.Utils;

namespace Turnierverwaltung.Server.Tests.Endpoints.Clubs;

public class GetClubsTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();

    [SuppressMessage("Usage", "CA1816")]
    public void Dispose()
    {
        _dbContext.Database.GetDbConnection().Dispose();
        _dbContext.Dispose();
    }

    [Fact]
    public async Task WhenClubsExist_ReturnsOkWithClubList()
    {
        var tournament = new Tournament
        {
            Id = 1,
            Name = "Test Tournament",
            Clubs =
            {
                new Club { Id = 1, Name = "Club 1" },
                new Club { Id = 2, Name = "Club 2" },
            },
        };

        _dbContext.Tournaments.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var result = await ClubEndpoints.GetClubs(_dbContext, 1);
        result
            .Should()
            .BeResult<Results<NotFound, Ok<List<ListClubDto>>>, Ok<List<ListClubDto>>>()
            .Subject.Value.Should()
            .NotBeNull()
            .And.HaveSameCount(tournament.Clubs)
            .And.BeInAscendingOrder(c => c.Name)
            .And.ThenBeInAscendingOrder(c => c.Id);
    }

    [Fact]
    public async Task WhenNoClubsExistForTournament_ReturnsEmptyList()
    {
        var tournament1 = new Tournament { Id = 1, Name = "Tournament 1" };
        var tournament2 = new Tournament
        {
            Id = 2,
            Name = "Tournament 2",
            Clubs =
            {
                new Club { Id = 1, Name = "Test Club" },
            },
        };

        _dbContext.AddRange(tournament1, tournament2);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var result = await ClubEndpoints.GetClubs(_dbContext, 1);
        result.Should().BeResult<Results<NotFound, Ok<List<ListClubDto>>>, Ok<List<ListClubDto>>>().Subject.Value.Should().NotBeNull().And.BeEmpty();
    }

    [Fact]
    public async Task WhenTournamentDoesNotExist_ReturnsNotFound()
    {
        var result = await ClubEndpoints.GetClubs(_dbContext, 1);
        result.Should().BeResult<Results<NotFound, Ok<List<ListClubDto>>>, NotFound>();
    }
}

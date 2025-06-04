using System.Diagnostics.CodeAnalysis;
using AwesomeAssertions;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Tests.Utils;

namespace Turnierverwaltung.Server.Tests.Endpoints.Tournaments;

public class DeleteTournamentTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();

    [SuppressMessage("Usage", "CA1816")]
    public void Dispose()
    {
        _dbContext.Database.GetDbConnection().Dispose();
        _dbContext.Dispose();
    }

    [Fact]
    public async Task WhenTournamentExists_ReturnsOk()
    {
        var tournament = new Tournament
        {
            Id = 1,
            Name = "Test Tournament",
            Date = DateTestUtils.GetTestDate(),
            TeamSize = 3,
        };

        _dbContext.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var result = await TournamentEndpoints.DeleteTournament(_dbContext, 1);
        result.Should().BeOfType<Ok>();

        var dbTournament = await _dbContext.Tournaments.SingleOrDefaultAsync(
            t => t.Id == tournament.Id,
            TestContext.Current.CancellationToken
        );
        dbTournament.Should().BeNull();
    }

    [Fact]
    public async Task WhenTournamentDoesNotExist_ReturnsNotFound()
    {
        var result = await TournamentEndpoints.DeleteTournament(_dbContext, 1);
        result.Should().BeOfType<NotFound>();
    }
}

using System.Diagnostics.CodeAnalysis;
using FluentAssertions;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Model.Transfer;
using Turnierverwaltung.Server.Model.Transfer.Tournament;
using Turnierverwaltung.Server.Tests.Utils;

namespace Turnierverwaltung.Server.Tests.Endpoints.Tournaments;

public class GetTournamentsTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();

    [SuppressMessage("Usage", "CA1816")]
    public void Dispose()
    {
        _dbContext.Database.GetDbConnection().Dispose();
        _dbContext.Dispose();
    }

    [Fact]
    public async Task WhenTournamentsExist_ReturnsOkWithTournamentList()
    {
        var tournaments = new List<Tournament>
        {
            new() { Id = 1, Date = DateTestUtils.GetTestDate(1) },
            new() { Id = 2, Date = DateTestUtils.GetTestDate(2) },
        };

        _dbContext.AddRange(tournaments);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var result = await TournamentEndpoints.GetTournaments(_dbContext);
        result
            .Should()
            .BeOfType<Ok<List<ListTournamentDto>>>()
            .Which.Value.Should()
            .NotBeNull()
            .And.HaveCount(tournaments.Count)
            .And.BeInDescendingOrder(dto => dto.Date)
            .And.ThenBeInAscendingOrder(dto => dto.Name)
            .And.ThenBeInAscendingOrder(dto => dto.Id);
    }

    [Fact]
    public async Task WhenNoTournamentsExist_ReturnsEmptyList()
    {
        var result = await TournamentEndpoints.GetTournaments(_dbContext);
        var okResult = result.Should().BeOfType<Ok<List<ListTournamentDto>>>().Subject;

        var responseData = okResult.Value;
        responseData.Should().BeEmpty();
    }
}

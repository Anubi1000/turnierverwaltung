using System.Diagnostics.CodeAnalysis;
using FluentAssertions;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Model.Transfer.Discipline;
using Turnierverwaltung.Server.Tests.Utils;

namespace Turnierverwaltung.Server.Tests.Endpoints.Disciplines;

public class GetDisciplinesTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();

    [SuppressMessage("Usage", "CA1816")]
    public void Dispose()
    {
        _dbContext.Database.GetDbConnection().Dispose();
        _dbContext.Dispose();
    }

    [Fact]
    public async Task WhenDisciplinesExist_ReturnsOkWithDisciplineList()
    {
        var tournament = new Tournament
        {
            Id = 1,
            Name = "Test Tournament",
            Disciplines =
            {
                new Discipline { Id = 1, Name = "Discipline 1" },
                new Discipline { Id = 2, Name = "Discipline 2" },
            },
        };

        _dbContext.Tournaments.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var result = await DisciplineEndpoints.GetDisciplines(_dbContext, 1);
        result
            .Should()
            .BeOfType<Ok<List<ListDisciplineDto>>>()
            .Which.Value.Should()
            .NotBeNull()
            .And.HaveSameCount(tournament.Disciplines)
            .And.BeInAscendingOrder(d => d.Name)
            .And.ThenBeInAscendingOrder(d => d.Id);
    }

    [Fact]
    public async Task WhenNoDisciplinesExist_ReturnsEmptyList()
    {
        var tournament1 = new Tournament { Id = 1, Name = "Tournament 1" };
        var tournament2 = new Tournament
        {
            Id = 2,
            Name = "Tournament 2",
            Disciplines =
            {
                new Discipline { Id = 1, Name = "Test Discipline" },
            },
        };

        _dbContext.AddRange(tournament1, tournament2);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var result = await DisciplineEndpoints.GetDisciplines(_dbContext, 1);
        result.Should().BeOfType<Ok<List<ListDisciplineDto>>>().Which.Value.Should().NotBeNull().And.BeEmpty();
    }

    [Fact]
    public async Task WhenTournamentDoesNotExist_ReturnsNotFound()
    {
        var result = await DisciplineEndpoints.GetDisciplines(_dbContext, 1);
        result.Should().BeOfType<NotFound>();
    }
}

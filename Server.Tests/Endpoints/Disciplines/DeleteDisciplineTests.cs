using System.Diagnostics.CodeAnalysis;
using AwesomeAssertions;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Tests.Utils;

namespace Turnierverwaltung.Server.Tests.Endpoints.Disciplines;

public class DeleteDisciplineTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();

    [SuppressMessage("Usage", "CA1816")]
    public void Dispose()
    {
        _dbContext.Database.GetDbConnection().Dispose();
        _dbContext.Dispose();
    }

    [Fact]
    public async Task WhenDisciplineExists_ReturnsOk()
    {
        var tournament = new Tournament
        {
            Id = 1,
            Disciplines =
            {
                new Discipline { Id = 1, Name = "Test Discipline" },
            },
        };
        _dbContext.Tournaments.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var result = await DisciplineEndpoints.DeleteDiscipline(_dbContext, NoOpScoreboardManager.Instance, 1);
        result.Should().BeResult<Results<NotFound, Ok>, Ok>();

        var discipline = await _dbContext.Disciplines.SingleOrDefaultAsync(
            d => d.Id == 1,
            TestContext.Current.CancellationToken
        );
        discipline.Should().BeNull();
    }

    [Fact]
    public async Task WhenDisciplineDoesNotExist_ReturnsNotFound()
    {
        var result = await DisciplineEndpoints.DeleteDiscipline(_dbContext, NoOpScoreboardManager.Instance, 1);
        result.Should().BeResult<Results<NotFound, Ok>, NotFound>();
    }
}

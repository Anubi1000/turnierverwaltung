using System.Diagnostics.CodeAnalysis;
using FluentAssertions;
using FluentAssertions.Equivalency;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Model.Transfer.Discipline;
using Turnierverwaltung.Server.Tests.Utils;

namespace Turnierverwaltung.Server.Tests.Endpoints.Disciplines;

public class GetDisciplineTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();

    [SuppressMessage("Usage", "CA1816")]
    public void Dispose()
    {
        _dbContext.Database.GetDbConnection().Dispose();
        _dbContext.Dispose();
    }

    [Fact]
    public async Task WhenDisciplineExists_ReturnsOkWithDisciplineDetails()
    {
        var tournament = new Tournament
        {
            Id = 1,
            Disciplines =
            {
                new Discipline
                {
                    Id = 1,
                    Name = "Test Discipline",
                    AmountOfBestRoundsToShow = 3,
                    AreGendersSeparated = true,
                    Values =
                    [
                        new Discipline.Value { Name = "Value1", IsAdded = true },
                        new Discipline.Value { Name = "Value2", IsAdded = false },
                    ],
                },
            },
        };

        _dbContext.Tournaments.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var result = await DisciplineEndpoints.GetDiscipline(_dbContext, 1);
        result
            .Should()
            .BeOfType<Ok<DisciplineDetailDto>>()
            .Which.Value.Should()
            .NotBeNull()
            .And.Satisfy<DisciplineDetailDto>(dto =>
            {
                var discipline = tournament.Disciplines.Single();

                dto.Should()
                    .BeEquivalentTo(
                        discipline,
                        options =>
                            options
                                .Including(d => d.Id)
                                .Including(d => d.TournamentId)
                                .Including(d => d.AmountOfBestRoundsToShow)
                                .Including(d => d.AreGendersSeparated)
                                .Including(d => d.Name)
                                .Including(d => d.Values)
                    );
            });
    }

    [Fact]
    public async Task WhenDisciplineDoesNotExist_ReturnsNotFound()
    {
        var result = await DisciplineEndpoints.GetDiscipline(_dbContext, 1);
        result.Should().BeOfType<NotFound>();
    }
}

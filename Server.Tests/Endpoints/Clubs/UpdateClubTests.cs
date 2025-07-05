using System.Diagnostics.CodeAnalysis;
using AwesomeAssertions;
using FluentValidation;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Model.Transfer.Club;
using Turnierverwaltung.Server.Model.Validation;
using Turnierverwaltung.Server.Tests.Utils;

namespace Turnierverwaltung.Server.Tests.Endpoints.Clubs;

public class UpdateClubTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();
    private readonly IValidator<ClubEditDto> _validator = new ClubEditDtoValidator();

    [SuppressMessage("Usage", "CA1816")]
    public void Dispose()
    {
        _dbContext.Database.GetDbConnection().Dispose();
        _dbContext.Dispose();
    }

    [Fact]
    public async Task WithValidData_ReturnsOk()
    {
        var tournament = new Tournament
        {
            Id = 1,
            Name = "Test Tournament",
            Clubs =
            {
                new Club { Id = 1, Name = "Old Name" },
            },
        };

        _dbContext.Tournaments.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var dto = new ClubEditDto("New Name");

        var result = await ClubEndpoints.UpdateClub(_dbContext, _validator, NoOpScoreboardManager.Instance, 1, dto);
        result.Should().BeResult<Results<NotFound, ValidationProblem, Ok>, Ok>();

        var updatedClub = await _dbContext.Clubs.SingleOrDefaultAsync(
            c => c.Id == 1,
            TestContext.Current.CancellationToken
        );

        updatedClub.Should().NotBeNull().And.BeEquivalentTo(dto);
    }

    [Fact]
    public async Task WithInvalidData_ReturnsValidationProblem()
    {
        var tournament = new Tournament
        {
            Id = 1,
            Name = "Test Tournament",
            Clubs =
            {
                new Club { Id = 1, Name = "Old Name" },
            },
        };

        _dbContext.Tournaments.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var dto = new ClubEditDto("");

        var result = await ClubEndpoints.UpdateClub(_dbContext, _validator, NoOpScoreboardManager.Instance, 1, dto);
        result
            .Should()
            .BeResult<Results<NotFound, ValidationProblem, Ok>, ValidationProblem>()
            .Which.ProblemDetails.Errors.Should()
            .HaveCount(1)
            .And.ContainKey("Name");
    }

    [Fact]
    public async Task WhenClubDoesNotExist_ReturnsNotFound()
    {
        var dto = new ClubEditDto("New Name");

        var result = await ClubEndpoints.UpdateClub(_dbContext, _validator, NoOpScoreboardManager.Instance, 1, dto);
        result.Should().BeResult<Results<NotFound, ValidationProblem, Ok>, NotFound>();
    }
}

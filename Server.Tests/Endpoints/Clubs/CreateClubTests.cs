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

public class CreateClubTests : IDisposable
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
    public async Task WithValidData_ReturnsOkWithClubId()
    {
        var tournament = new Tournament { Id = 1, Name = "Test Tournament" };
        _dbContext.Tournaments.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var dto = new ClubEditDto("Test Club");

        var result = await ClubEndpoints.CreateClub(_dbContext, _validator, 1, dto);
        var okResult = result.Should().BeResult<Results<NotFound, ValidationProblem, Ok<int>>, Ok<int>>().Subject;

        var clubId = okResult.Value;
        var club = await _dbContext.Clubs.SingleOrDefaultAsync(
            c => c.Id == clubId,
            TestContext.Current.CancellationToken
        );

        club.Should().NotBeNull().And.BeEquivalentTo(dto);
        club.TournamentId.Should().Be(1);
    }

    [Fact]
    public async Task WithInvalidData_ReturnsValidationProblem()
    {
        var tournament = new Tournament { Id = 1, Name = "Test Tournament" };
        _dbContext.Tournaments.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var dto = new ClubEditDto("");

        var result = await ClubEndpoints.CreateClub(_dbContext, _validator, 1, dto);
        result
            .Should()
            .BeResult<Results<NotFound, ValidationProblem, Ok<int>>, ValidationProblem>()
            .Subject.ProblemDetails.Errors.Should()
            .HaveCount(1)
            .And.ContainKey("Name");
    }

    [Fact]
    public async Task WhenTournamentDoesNotExist_ReturnsNotFound()
    {
        var dto = new ClubEditDto("Test Club");

        var result = await ClubEndpoints.CreateClub(_dbContext, _validator, 1, dto);
        result.Should().BeResult<Results<NotFound, ValidationProblem, Ok<int>>, NotFound>();
    }
}

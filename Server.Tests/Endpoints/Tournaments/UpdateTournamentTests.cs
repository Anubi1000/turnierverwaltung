using System.Diagnostics.CodeAnalysis;
using AwesomeAssertions;
using FluentValidation;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Model.Transfer.Tournament;
using Turnierverwaltung.Server.Model.Validation;
using Turnierverwaltung.Server.Tests.Utils;

namespace Turnierverwaltung.Server.Tests.Endpoints.Tournaments;

public class UpdateTournamentTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();
    private readonly IValidator<TournamentEditDto> _validator = new TournamentEditDtoValidator();

    [SuppressMessage("Usage", "CA1816")]
    public void Dispose()
    {
        _dbContext.Database.GetDbConnection().Dispose();
        _dbContext.Dispose();
    }

    [Fact]
    public async Task WithDataValid_ReturnsOk()
    {
        var tournament = new Tournament
        {
            Id = 1,
            Name = "Old Name",
            Date = DateTestUtils.GetTestDate(),
            TeamSize = 3,
        };
        _dbContext.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var dto = new TournamentEditDto("New Name", DateTestUtils.GetTestDate(1), 3, true);

        var result = await TournamentEndpoints.UpdateTournament(
            _dbContext,
            _validator,
            NoOpScoreboardManager.Instance,
            1,
            dto
        );
        result.Should().BeResult<Results<NotFound, ValidationProblem, Ok>, Ok>();

        var updatedTournament = await _dbContext.Tournaments.SingleOrDefaultAsync(
            t => t.Id == 1,
            TestContext.Current.CancellationToken
        );

        updatedTournament.Should().NotBeNull().And.BeEquivalentTo(dto);
    }

    [Fact]
    public async Task WithInvalidData_ReturnsValidationProblem()
    {
        var tournament = new Tournament
        {
            Id = 1,
            Name = "Old Name",
            Date = DateTestUtils.GetTestDate(),
            TeamSize = 3,
        };
        _dbContext.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var dto = new TournamentEditDto("", DateTestUtils.GetTestDate(), 5, true);

        var result = await TournamentEndpoints.UpdateTournament(
            _dbContext,
            _validator,
            NoOpScoreboardManager.Instance,
            1,
            dto
        );
        result
            .Should()
            .BeResult<Results<NotFound, ValidationProblem, Ok>, ValidationProblem>()
            .Which.ProblemDetails.Errors.Should()
            .HaveCount(2)
            .And.ContainKeys("Name", "TeamSize");
    }

    [Fact]
    public async Task WhenTournamentDoesNotExist_ReturnsNotFound()
    {
        var dto = new TournamentEditDto("New Name", DateTestUtils.GetTestDate(), 3, true);

        var result = await TournamentEndpoints.UpdateTournament(
            _dbContext,
            _validator,
            NoOpScoreboardManager.Instance,
            1,
            dto
        );
        result.Should().BeResult<Results<NotFound, ValidationProblem, Ok>, NotFound>();
    }
}

using System.Diagnostics.CodeAnalysis;
using FluentAssertions;
using FluentValidation;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Model.Transfer;
using Turnierverwaltung.Server.Model.Transfer.Tournament;
using Turnierverwaltung.Server.Model.Validation;
using Turnierverwaltung.Server.Tests.Utils;

namespace Turnierverwaltung.Server.Tests.Endpoints.Tournaments;

public class CreateTournamentTests : IDisposable
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
    public async Task WithValidData_ReturnsOkWithTournamentId()
    {
        var dto = new TournamentEditDto("Test Tournament", DateTestUtils.GetTestDate(), 3, true);

        var result = await TournamentEndpoints.CreateTournament(_dbContext, _validator, dto);
        var okResult = result.Should().BeOfType<Ok<int>>().Subject;

        var tournamentId = okResult.Value;
        var tournament = await _dbContext
            .Tournaments.Where(t => t.Id == tournamentId)
            .SingleOrDefaultAsync(TestContext.Current.CancellationToken);

        tournament.Should().NotBeNull().And.BeEquivalentTo(dto);
    }

    [Fact]
    public async Task WithInvalidData_ReturnsValidationProblem()
    {
        var dto = new TournamentEditDto("", DateTestUtils.GetTestDate(), 3, true);

        var result = await TournamentEndpoints.CreateTournament(_dbContext, _validator, dto);
        result
            .Should()
            .BeOfType<ValidationProblem>()
            .Which.ProblemDetails.Errors.Should()
            .HaveCount(1)
            .And.ContainKey("Name");
    }
}

using System.Diagnostics.CodeAnalysis;
using AwesomeAssertions;
using FluentValidation;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Model.Transfer.Discipline;
using Turnierverwaltung.Server.Model.Validation;
using Turnierverwaltung.Server.Tests.Utils;

namespace Turnierverwaltung.Server.Tests.Endpoints.Disciplines;

public class CreateDisciplineTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();
    private readonly IValidator<DisciplineEditDto> _validator = new DisciplineEditDtoValidator();

    [SuppressMessage("Usage", "CA1816")]
    public void Dispose()
    {
        _dbContext.Database.GetDbConnection().Dispose();
        _dbContext.Dispose();
    }

    [Fact]
    public async Task WithValidData_ReturnsOkWithDisciplineId()
    {
        var tournament = new Tournament { Id = 1, Name = "Test Tournament" };
        _dbContext.Tournaments.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var dto = new DisciplineEditDto(
            "Test Discipline",
            3,
            true,
            true,
            [new DisciplineEditDto.Value("Value1", true), new DisciplineEditDto.Value("Value2", false)]
        );

        var result = await DisciplineEndpoints.CreateDiscipline(
            _dbContext,
            _validator,
            NoOpScoreboardManager.Instance,
            1,
            dto
        );
        var okResult = result.Should().BeResult<Results<NotFound, ValidationProblem, Ok<int>>, Ok<int>>().Subject;

        var disciplineId = okResult.Value;
        var discipline = await _dbContext.Disciplines.SingleOrDefaultAsync(
            d => d.Id == disciplineId,
            TestContext.Current.CancellationToken
        );

        discipline.Should().NotBeNull().And.BeEquivalentTo(dto);
        discipline.TournamentId.Should().Be(1);
    }

    [Fact]
    public async Task WithInvalidData_ReturnsValidationProblem()
    {
        var tournament = new Tournament { Id = 1, Name = "Test Tournament" };
        _dbContext.Tournaments.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var dto = new DisciplineEditDto("", 3, true, true, [new DisciplineEditDto.Value("Value1", true)]);

        var result = await DisciplineEndpoints.CreateDiscipline(
            _dbContext,
            _validator,
            NoOpScoreboardManager.Instance,
            1,
            dto
        );
        result
            .Should()
            .BeResult<Results<NotFound, ValidationProblem, Ok<int>>, ValidationProblem>()
            .Which.ProblemDetails.Errors.Should()
            .HaveCount(1)
            .And.ContainKey("Name");
    }

    [Fact]
    public async Task WhenTournamentDoesNotExist_ReturnsNotFound()
    {
        var dto = new DisciplineEditDto(
            "Test Discipline",
            3,
            true,
            true,
            [new DisciplineEditDto.Value("Value1", true)]
        );

        var result = await DisciplineEndpoints.CreateDiscipline(
            _dbContext,
            _validator,
            NoOpScoreboardManager.Instance,
            1,
            dto
        );
        result.Should().BeResult<Results<NotFound, ValidationProblem, Ok<int>>, NotFound>();
    }
}

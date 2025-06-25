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

public class UpdateDisciplineTests : IDisposable
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
    public async Task WithValidData_ReturnsOk()
    {
        var tournament = new Tournament
        {
            Id = 1,
            Name = "Test Tournament",
            Disciplines =
            {
                new Discipline
                {
                    Id = 1,
                    Name = "Old Discipline",
                    Values = [new Discipline.Value { Name = "Old Value", IsAdded = true }],
                },
            },
        };

        _dbContext.Tournaments.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var dto = new DisciplineEditDto(
            "New Discipline",
            2,
            true,
            true,
            [new DisciplineEditDto.Value("New Value", false)]
        );

        var result = await DisciplineEndpoints.UpdateDiscipline(_dbContext, _validator, 1, dto);
        result.Should().BeResult<Results<NotFound, ValidationProblem, Ok>, Ok>();

        var updatedDiscipline = await _dbContext.Disciplines.SingleOrDefaultAsync(
            d => d.Id == 1,
            TestContext.Current.CancellationToken
        );

        updatedDiscipline.Should().NotBeNull().And.BeEquivalentTo(dto);
    }

    [Fact]
    public async Task WithInvalidData_ReturnsValidationProblem()
    {
        var tournament = new Tournament
        {
            Id = 1,
            Name = "Test Tournament",
            Disciplines =
            {
                new Discipline
                {
                    Id = 1,
                    Name = "Old Discipline",
                    Values = [new Discipline.Value { Name = "Value 1", IsAdded = true }],
                },
            },
        };
        _dbContext.Tournaments.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var dto = new DisciplineEditDto("", 1, true, true, [new DisciplineEditDto.Value("Value 1", true)]);

        var result = await DisciplineEndpoints.UpdateDiscipline(_dbContext, _validator, 1, dto);
        result
            .Should()
            .BeResult<Results<NotFound, ValidationProblem, Ok>, ValidationProblem>()
            .Which.ProblemDetails.Errors.Should()
            .HaveCount(1)
            .And.ContainKey("Name");
    }

    [Fact]
    public async Task WhenDisciplineDoesNotExist_ReturnsNotFound()
    {
        var dto = new DisciplineEditDto(
            "New Discipline",
            1,
            true,
            true,
            [new DisciplineEditDto.Value("Value 1", true)]
        );

        var result = await DisciplineEndpoints.UpdateDiscipline(_dbContext, _validator, 1, dto);
        result.Should().BeResult<Results<NotFound, ValidationProblem, Ok>, NotFound>();
    }
}

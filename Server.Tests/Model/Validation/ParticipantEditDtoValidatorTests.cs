using System.Diagnostics.CodeAnalysis;
using FluentValidation;
using FluentValidation.TestHelper;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Model.Transfer.Participant;
using Turnierverwaltung.Server.Model.Validation;
using Turnierverwaltung.Server.Tests.Utils;

namespace Turnierverwaltung.Server.Tests.Model.Validation;

public class ParticipantEditDtoValidatorTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext;
    private readonly ParticipantEditDtoValidator _validator;

    public ParticipantEditDtoValidatorTests()
    {
        _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();
        _validator = new ParticipantEditDtoValidator(_dbContext);
    }

    [SuppressMessage("Usage", "CA1816")]
    public void Dispose()
    {
        _dbContext.Dispose();
    }

    private static ParticipantEditDto CreateValidDto(
        string name = "Valid Name",
        int startNumber = 1,
        Gender gender = Gender.Male,
        int clubId = 1
    )
    {
        return new ParticipantEditDto(name, startNumber, gender, clubId);
    }

    private static ValidationContext<ParticipantEditDto> CreateValidationContext(
        ParticipantEditDto dto,
        int tournamentId
    )
    {
        var context = new ValidationContext<ParticipantEditDto>(dto)
        {
            RootContextData = { [ParticipantEditDtoValidator.TournamentIdKey] = tournamentId },
        };
        return context;
    }

    [Fact]
    public async Task WhenNameIsEmpty_HasError()
    {
        var dto = CreateValidDto("");
        var context = CreateValidationContext(dto, 1);
        var result = await _validator.TestValidateAsync(context, TestContext.Current.CancellationToken);
        result.ShouldHaveValidationErrorFor(x => x.Name);
    }

    [Fact]
    public async Task WhenNameExceedsMaxLength_HasError()
    {
        var dto = CreateValidDto(new string('A', 151));
        var context = CreateValidationContext(dto, 1);
        var result = await _validator.TestValidateAsync(context, TestContext.Current.CancellationToken);
        result.ShouldHaveValidationErrorFor(x => x.Name);
    }

    [Theory]
    [InlineData(0)]
    [InlineData(100_001)]
    public async Task WhenStartNumberIsOutOfRange_HasError(int startNumber)
    {
        var dto = CreateValidDto(startNumber: startNumber);
        var context = CreateValidationContext(dto, 1);
        var result = await _validator.TestValidateAsync(context, TestContext.Current.CancellationToken);
        result.ShouldHaveValidationErrorFor(x => x.StartNumber);
    }

    [Fact]
    public async Task WhenClubDoesNotExistInTournament_HasError()
    {
        var context = CreateValidationContext(CreateValidDto(), 1);

        var result = await _validator.TestValidateAsync(context, TestContext.Current.CancellationToken);
        result.ShouldHaveValidationErrorFor(x => x.ClubId);
    }

    [Fact]
    public async Task WhenFieldsAreValid_HasNoError()
    {
        _dbContext.Add(new Tournament { Id = 1 });
        _dbContext.Add(new Club { Id = 1, TournamentId = 1 });
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var context = CreateValidationContext(CreateValidDto(), 1);

        var result = await _validator.TestValidateAsync(context, TestContext.Current.CancellationToken);
        result.ShouldNotHaveAnyValidationErrors();
    }
}

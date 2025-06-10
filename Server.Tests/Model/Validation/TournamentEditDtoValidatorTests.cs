using FluentValidation;
using FluentValidation.TestHelper;
using Turnierverwaltung.Server.Model.Transfer.Tournament;
using Turnierverwaltung.Server.Model.Validation;

namespace Turnierverwaltung.Server.Tests.Model.Validation;

public class TournamentEditDtoValidatorTests
{
    private readonly TournamentEditDtoValidator _validator = new();

    private static TournamentEditDto CreateValidDto(
        string name = "Valid Name",
        DateTime? date = null,
        int teamSize = 3,
        bool isTeamSizeFixed = true
    )
    {
        return new TournamentEditDto(name, date ?? DateTime.Now, teamSize, isTeamSizeFixed);
    }

    [Fact]
    public void WhenNameIsEmpty_HasError()
    {
        var model = CreateValidDto("");
        var result = _validator.TestValidate(model);
        result.ShouldHaveValidationErrorFor(x => x.Name);
    }

    [Fact]
    public void WhenNameExceedsMaxLength_HasError()
    {
        var model = CreateValidDto(new string('a', 151));
        var result = _validator.TestValidate(model);
        result.ShouldHaveValidationErrorFor(x => x.Name);
    }

    [Fact]
    public void WhenDateIsMinValue_HasError()
    {
        var model = CreateValidDto(date: DateTime.MinValue);
        var result = _validator.TestValidate(model);
        result.ShouldHaveValidationErrorFor(x => x.Date);
    }

    [Theory]
    [InlineData(1)]
    [InlineData(26)]
    public void WhenTeamSizeIsOutOfRange_HasError(int teamSize)
    {
        var model = CreateValidDto(teamSize: teamSize);
        var result = _validator.TestValidate(model);
        result.ShouldHaveValidationErrorFor(x => x.TeamSize);
    }

    [Fact]
    public void WhenTeamSizeChangedAndTeamSizeIsFixed_HasError()
    {
        var model = CreateValidDto(teamSize: 5);
        var context = new ValidationContext<TournamentEditDto>(model)
        {
            RootContextData =
            {
                [TournamentEditDtoValidator.PreviousTeamSizeKey] = 3,
                [TournamentEditDtoValidator.PreviousIsTeamSizeFixedKey] = true,
            },
        };
        var result = _validator.TestValidate(context);
        result.ShouldHaveValidationErrorFor(x => x.TeamSize);
    }

    [Fact]
    public void WhenTeamSizeChangedAndTeamSizeIsNotFixed_HasNoError()
    {
        var model = CreateValidDto(teamSize: 5, isTeamSizeFixed: false);
        var context = new ValidationContext<TournamentEditDto>(model)
        {
            RootContextData =
            {
                [TournamentEditDtoValidator.PreviousTeamSizeKey] = 3,
                [TournamentEditDtoValidator.PreviousIsTeamSizeFixedKey] = false,
            },
        };
        var result = _validator.TestValidate(context);
        result.ShouldNotHaveValidationErrorFor(x => x.TeamSize);
    }

    [Fact]
    public void WhenTeamSizeDoesNotChange_HasNoError()
    {
        var model = CreateValidDto(teamSize: 5);
        var context = new ValidationContext<TournamentEditDto>(model)
        {
            RootContextData = { [TournamentEditDtoValidator.PreviousTeamSizeKey] = 5 },
        };
        var result = _validator.TestValidate(context);
        result.ShouldNotHaveValidationErrorFor(x => x.TeamSize);
    }

    [Fact]
    public void WhenTeamSizeFixedChanged_HasError()
    {
        var model = CreateValidDto(isTeamSizeFixed: true);
        var context = new ValidationContext<TournamentEditDto>(model)
        {
            RootContextData = { [TournamentEditDtoValidator.PreviousIsTeamSizeFixedKey] = false },
        };
        var result = _validator.TestValidate(context);
        result.ShouldHaveValidationErrorFor(x => x.IsTeamSizeFixed);
    }

    [Fact]
    public void WhenTeamSizeFixedDoesNotChange_HasNoError()
    {
        var model = CreateValidDto(isTeamSizeFixed: true);
        var context = new ValidationContext<TournamentEditDto>(model)
        {
            RootContextData = { [TournamentEditDtoValidator.PreviousIsTeamSizeFixedKey] = true },
        };
        var result = _validator.TestValidate(context);
        result.ShouldNotHaveValidationErrorFor(x => x.IsTeamSizeFixed);
    }

    [Fact]
    public void WhenFieldsAreValid_HasNoError()
    {
        var model = CreateValidDto();
        var result = _validator.TestValidate(model);
        result.ShouldNotHaveAnyValidationErrors();
    }
}

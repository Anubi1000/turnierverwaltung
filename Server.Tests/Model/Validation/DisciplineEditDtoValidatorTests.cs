using FluentValidation.TestHelper;
using Turnierverwaltung.Server.Model.Transfer.Discipline;
using Turnierverwaltung.Server.Model.Validation;

namespace Turnierverwaltung.Server.Tests.Model.Validation;

public class DisciplineEditDtoValidatorTests
{
    private readonly DisciplineEditDtoValidator _validator = new();

    private static DisciplineEditDto CreateValidDto(
        string name = "Valid Name",
        int amountOfBestRoundsToShow = 3,
        List<DisciplineEditDto.Value>? values = null
    )
    {
        values ??= [new DisciplineEditDto.Value("Valid Value", true)];
        return new DisciplineEditDto(name, amountOfBestRoundsToShow, false, true, values);
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

    [Theory]
    [InlineData(0)]
    [InlineData(6)]
    public void WhenAmountOfBestRoundsToShowIsOutOfRange_HasError(int amountOfBestRoundsToShow)
    {
        var model = CreateValidDto(amountOfBestRoundsToShow: amountOfBestRoundsToShow);
        var result = _validator.TestValidate(model);
        result.ShouldHaveValidationErrorFor(x => x.AmountOfBestRoundsToShow);
    }

    [Fact]
    public void WhenValuesIsEmpty_HasError()
    {
        var model = CreateValidDto(values: []);
        var result = _validator.TestValidate(model);
        result.ShouldHaveValidationErrorFor(x => x.Values);
    }

    [Fact]
    public void WhenValuesHasInvalidEntries_HasError()
    {
        var values = new List<DisciplineEditDto.Value> { new("", false) };

        var model = CreateValidDto(values: values);
        var result = _validator.TestValidate(model);
        result.ShouldHaveValidationErrorFor("Values[0].Name");
    }

    [Fact]
    public void WhenFieldsAreValid_HasNoError()
    {
        var model = CreateValidDto();
        var result = _validator.TestValidate(model);
        result.ShouldNotHaveAnyValidationErrors();
    }
}

using FluentValidation.TestHelper;
using Turnierverwaltung.Server.Model.Transfer.Discipline;
using Turnierverwaltung.Server.Model.Validation;

namespace Turnierverwaltung.Server.Tests.Model.Validation;

public class DisciplineEditDtoValueValidatorTests
{
    private readonly DisciplineEditDtoValueValidator _validator = new();

    [Fact]
    public void WhenNameIsEmpty_HasError()
    {
        var model = new DisciplineEditDto.Value("", false);
        var result = _validator.TestValidate(model);
        result.ShouldHaveValidationErrorFor(x => x.Name);
    }

    [Fact]
    public void WhenNameExceedsMaxLength_HasError()
    {
        var model = new DisciplineEditDto.Value(new string('a', 151), false);
        var result = _validator.TestValidate(model);
        result.ShouldHaveValidationErrorFor(x => x.Name);
    }

    [Fact]
    public void WhenFieldsAreValid_HasNoError()
    {
        var model = new DisciplineEditDto.Value("Valid Name", false);
        var result = _validator.TestValidate(model);
        result.ShouldNotHaveValidationErrorFor(x => x.Name);
    }
}

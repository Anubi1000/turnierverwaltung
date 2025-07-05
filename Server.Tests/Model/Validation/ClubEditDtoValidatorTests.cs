using FluentValidation.TestHelper;
using Turnierverwaltung.Server.Model.Transfer.Club;
using Turnierverwaltung.Server.Model.Validation;

namespace Turnierverwaltung.Server.Tests.Model.Validation;

public class ClubEditDtoValidatorTests
{
    private readonly ClubEditDtoValidator _validator = new();

    [Fact]
    public void WhenNameIsEmpty_HasError()
    {
        var model = new ClubEditDto("");
        var result = _validator.TestValidate(model);
        result.ShouldHaveValidationErrorFor(x => x.Name);
    }

    [Fact]
    public void WhenNameExceedsMaxLength_HasError()
    {
        var model = new ClubEditDto(new string('a', 151));
        var result = _validator.TestValidate(model);
        result.ShouldHaveValidationErrorFor(x => x.Name);
    }

    [Fact]
    public void WhenFieldsAreValid_HasNoError()
    {
        var model = new ClubEditDto("Valid Name");
        var result = _validator.TestValidate(model);
        result.ShouldNotHaveValidationErrorFor(x => x.Name);
    }
}

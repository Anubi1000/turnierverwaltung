using FluentValidation;
using Turnierverwaltung.Server.Model.Transfer.Club;

namespace Turnierverwaltung.Server.Model.Validation;

public class ClubEditDtoValidator : AbstractValidator<ClubEditDto>
{
    public ClubEditDtoValidator()
    {
        RuleFor(x => x.Name).MustBeValidName();
    }
}

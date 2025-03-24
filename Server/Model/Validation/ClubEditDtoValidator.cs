using FluentValidation;
using Turnierverwaltung.Server.Model.Transfer.Club;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Model.Validation;

public class ClubEditDtoValidator : AbstractValidator<ClubEditDto>
{
    public ClubEditDtoValidator()
    {
        RuleFor(x => x.Name).NotEmpty().MaximumLength(Constants.MaxNameLength);
    }
}

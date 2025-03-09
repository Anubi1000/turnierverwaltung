using FluentValidation;
using Turnierverwaltung.Server.Model.Transfer.Discipline;

namespace Turnierverwaltung.Server.Model.Validation;

public class DisciplineEditDtoValueValidator : AbstractValidator<DisciplineEditDto.Value>
{
    public DisciplineEditDtoValueValidator()
    {
        RuleFor(x => x.Name).NotEmpty().MaximumLength(150);
    }
}

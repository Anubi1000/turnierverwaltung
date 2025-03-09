using FluentValidation;
using Turnierverwaltung.Server.Model.Transfer.Discipline;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Model.Validation;

public class DisciplineEditDtoValidator : AbstractValidator<DisciplineEditDto>
{
    public const string PreviousValueAmountKey = "PreviousValueAmount";

    public DisciplineEditDtoValidator()
    {
        RuleFor(x => x.Name).NotEmpty().MaximumLength(150);

        RuleFor(x => x.AmountOfBestRoundsToShow).InclusiveBetween(1, 5);

        RuleFor(x => x.Values).NotEmpty().ForEach(x => x.SetValidator(new DisciplineEditDtoValueValidator()));

        RuleFor(x => x.Values.Count).MustNotChange(PreviousValueAmountKey);
    }
}

using FluentValidation;
using Turnierverwaltung.Server.Model.Transfer.Participant;

namespace Turnierverwaltung.Server.Model.Validation;

public class ParticipantResultEditDtoValidator : AbstractValidator<ParticipantResultEditDto>
{
    public const string DisciplineValueCountKey = "DisciplineValueCount";

    public ParticipantResultEditDtoValidator()
    {
        RuleForEach(x => x.Rounds)
            .ChildRules(result =>
            {
                result
                    .RuleFor(x => x.Values)
                    .Must((_, value, context) => value.Count == (int)context.RootContextData[DisciplineValueCountKey])
                    .WithMessage(
                        "The count of items in '{PropertyName}' needs to be equal to the number of values in the discipline"
                    );
            });
    }
}

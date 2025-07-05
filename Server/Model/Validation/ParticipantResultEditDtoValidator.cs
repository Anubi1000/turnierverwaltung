using FluentValidation;
using Turnierverwaltung.Server.Model.Transfer.Participant;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Model.Validation;

public class ParticipantResultEditDtoValidator : AbstractValidator<ParticipantResultEditDto>
{
    public const string DisciplineValueCountKey = "DisciplineValueCount";

    public ParticipantResultEditDtoValidator()
    {
        RuleForEach(x => x.Rounds)
            .ChildRules(child =>
            {
                child.RuleFor(x => x.Values.Count).MustNotChange(DisciplineValueCountKey);
            });
    }
}

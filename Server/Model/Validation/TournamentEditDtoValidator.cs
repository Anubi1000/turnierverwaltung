using FluentValidation;
using Turnierverwaltung.Server.Model.Transfer.Tournament;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Model.Validation;

public class TournamentEditDtoValidator : AbstractValidator<TournamentEditDto>
{
    public const string PreviousTeamSizeKey = "PreviousTeamSize";

    public const string PreviousIsTeamSizeFixedKey = "PreviousIsTeamSizeFixed";

    public TournamentEditDtoValidator()
    {
        // Validate that Name is a valid name.
        RuleFor(tournament => tournament.Name).MustBeValidName();

        // Validate that TeamSize is between 2 and 25 and has not changed from its previous value when IsTeamSizeFixed is true.
        RuleFor(tournament => tournament.TeamSize)
            .GreaterThanOrEqualTo(2)
            .LessThanOrEqualTo(25)
            .Must(
                (_, value, context) =>
                {
                    if (
                        context.RootContextData.TryGetValue(PreviousTeamSizeKey, out var previousTeamSize)
                        != context.RootContextData.TryGetValue(PreviousIsTeamSizeFixedKey, out var isTeamSizeFixed)
                    )
                        throw new ArgumentException(
                            "If one of PreviousTeamSizeKey and PreviousIsTeamSizeFixedKey is defined, both need to be defined"
                        );

                    return !(isTeamSizeFixed is true && !value.Equals(previousTeamSize));
                }
            )
            .WithMessage("'TeamSize' is not allowed to change if 'IsTeamSizeFixed' is true.");

        RuleFor(tournament => tournament.IsTeamSizeFixed).NotNull().MustNotChange(PreviousIsTeamSizeFixedKey);
    }
}

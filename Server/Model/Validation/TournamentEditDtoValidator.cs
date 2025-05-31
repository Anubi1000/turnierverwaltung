using FluentValidation;
using Turnierverwaltung.Server.Model.Transfer.Tournament;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Model.Validation;

public class TournamentEditDtoValidator : AbstractValidator<TournamentEditDto>
{
    /// <summary>
    ///     Key used to store and retrieve the previous value of TeamSize from the validation context.
    /// </summary>
    public const string PreviousTeamSizeKey = "PreviousTeamSize";

    public const string PreviousIsTeamSizeFixedKey = "PreviousIsTeamSizeFixed";

    public TournamentEditDtoValidator()
    {
        // Validate that Name is a valid name.
        RuleFor(tournament => tournament.Name).MustBeValidName();

        // Validate that Date is not set to DateTime.MinValue.
        RuleFor(tournament => tournament.Date)
            .Must(date => !date.Equals(DateTime.MinValue))
            .WithMessage("'{PropertyName}' must not be undefined.");

        // Validate that TeamSize is between 2 and 25 and has not changed from its previous value when IsTeamSizeFixed is true.
        RuleFor(tournament => tournament.TeamSize)
            .GreaterThanOrEqualTo(2)
            .LessThanOrEqualTo(25)
            .Must(
                (_, value, context) =>
                {
                    if (
                        context.RootContextData.TryGetValue(PreviousIsTeamSizeFixedKey, out var isTeamSizeFixed)
                        && (bool)isTeamSizeFixed
                    )
                        return !context.RootContextData.TryGetValue(PreviousTeamSizeKey, out var previousTeamSize)
                            || value.Equals(previousTeamSize);

                    return true;
                }
            )
            .WithMessage("\'{PropertyName}\' is not allowed to change if IsTeamSizeFixed is true.");

        RuleFor(tournament => tournament.IsTeamSizeFixed).NotNull().MustNotChange(PreviousIsTeamSizeFixedKey);
    }
}

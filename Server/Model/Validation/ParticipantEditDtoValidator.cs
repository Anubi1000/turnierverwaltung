using FluentValidation;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Model.Transfer.Participant;

namespace Turnierverwaltung.Server.Model.Validation;

public class ParticipantEditDtoValidator : AbstractValidator<ParticipantEditDto>
{
    public const string TournamentIdKey = "TournamentId";

    public ParticipantEditDtoValidator(ApplicationDbContext db)
    {
        RuleFor(x => x.Name).NotEmpty().MaximumLength(150);

        RuleFor(x => x.StartNumber).GreaterThanOrEqualTo(1).LessThanOrEqualTo(100_000);

        RuleFor(x => x.ClubId)
            .MustAsync(
                (_, clubId, context, cancellationToken) =>
                    db.Clubs.AnyAsync(
                        c => c.TournamentId == (int)context.RootContextData[TournamentIdKey] && c.Id == clubId,
                        cancellationToken
                    )
            )
            .WithMessage("The specified club needs to exists in the tournament.");
    }
}

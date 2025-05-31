using FluentValidation;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Model.Transfer.Team;

namespace Turnierverwaltung.Server.Model.Validation;

public class TeamEditDtoValidator : AbstractValidator<TeamEditDto>
{
    public const string TournamentIdKey = "TournamentId";

    public TeamEditDtoValidator(ApplicationDbContext db)
    {
        RuleFor(x => x.Name).MustBeValidName();

        RuleFor(x => x.StartNumber).GreaterThanOrEqualTo(1).LessThanOrEqualTo(100_000);

        RuleFor(x => x.Members)
            .MustAsync(
                async (_, members, context, cancellationToken) =>
                {
                    var tournament = await db
                        .Tournaments.AsNoTracking()
                        .Where(t => t.Id == (int)context.RootContextData[TournamentIdKey])
                        .FirstOrDefaultAsync(cancellationToken);
                    if (tournament is null)
                        return false;

                    if (tournament.IsTeamSizeFixed)
                        return members.Count == tournament.TeamSize;

                    return true;
                }
            ).WithMessage("\'{PropertyName}\' needs to contain the same amount of members as specified in the tournament.");

        RuleForEach(x => x.Members)
            .MustAsync(
                (_, participantId, context, cancellationToken) =>
                    db.Participants.AnyAsync(
                        participant =>
                            participant.TournamentId == (int)context.RootContextData[TournamentIdKey]
                            && participant.Id == participantId,
                        cancellationToken
                    )
            );

        RuleForEach(x => x.ParticipatingDisciplines)
            .MustAsync(
                (_, teamDisciplineId, context, cancellationToken) =>
                    db.TeamDisciplines.AnyAsync(
                        teamDiscipline =>
                            teamDiscipline.TournamentId == (int)context.RootContextData[TournamentIdKey]
                            && teamDiscipline.Id == teamDisciplineId,
                        cancellationToken
                    )
            );
    }
}

using FluentValidation;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Model.Transfer.TeamDiscipline;

namespace Turnierverwaltung.Server.Model.Validation;

public class TeamDisciplineEditDtoValidator : AbstractValidator<TeamDisciplineEditDto>
{
    public const string TournamentIdKey = "TournamentId";

    public TeamDisciplineEditDtoValidator(ApplicationDbContext db)
    {
        RuleFor(x => x.Name).NotEmpty().MaximumLength(150);
        RuleFor(x => x.BasedOn).NotEmpty();
        RuleForEach(x => x.BasedOn)
            .MustAsync(
                (
                    (_, disciplineId, context, cancellationToken) =>
                        db.Disciplines.AnyAsync(
                            t => t.TournamentId == (int)context.RootContextData[TournamentIdKey] && t.Id == disciplineId,
                            cancellationToken
                        )
                )
            );
    }
}

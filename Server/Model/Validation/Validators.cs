using System.Text.RegularExpressions;
using FluentValidation;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Model.Validation;

public static partial class Validators
{
    [GeneratedRegex(@"^[\w.\- ']+$", RegexOptions.None, 100, "de-DE")]
    private static partial Regex NameRegex();

    public static IRuleBuilderOptions<T, string> MustBeValidName<T>(this IRuleBuilder<T, string> ruleBuilder)
    {
        return ruleBuilder
            .NotEmpty()
            .MaximumLength(Constants.MaxNameLength)
            .Must(name => NameRegex().IsMatch(name))
            .WithMessage("'{PropertyName}' needs to be a valid name.");
    }
}

using FluentValidation;
using Turnierverwaltung.Server.Model.Transfer;

namespace Turnierverwaltung.Server.Model.Validation;

public class WordDocGenerationDtoValidator : AbstractValidator<WordDocGenerationDto>
{
    public WordDocGenerationDtoValidator()
    {
        RuleFor(x => x.TablesToExport)
            .NotEmpty()
            .Must(tables => tables.All(table => !string.IsNullOrWhiteSpace(table)))
            .WithMessage("'{PropertyName}' cannot contain null or whitespace values.");

        RuleFor(x => x.SeparateDocuments).NotNull();
    }
}

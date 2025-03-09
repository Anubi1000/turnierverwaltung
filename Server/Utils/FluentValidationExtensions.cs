using FluentValidation;

namespace Turnierverwaltung.Server.Utils;

public static class FluentValidationExtensions
{
    /// <summary>
    /// Adds a validation rule to ensure that a property's value has not changed compared to a previous value stored in the validation context.
    /// </summary>
    /// <typeparam name="T">The type of the object being validated.</typeparam>
    /// <typeparam name="TProperty">The type of the property being validated.</typeparam>
    /// <param name="ruleBuilder">The rule builder for defining the validation rules.</param>
    /// <param name="contextKey">The key used to retrieve the previous value from the validation context's root data.</param>
    /// <param name="message">The custom error message to use if the property value has changed.
    /// Defaults to "'{PropertyName}' is not allowed to change."</param>
    /// <returns>An <see cref="IRuleBuilderOptions{T, TProperty}"/> for chaining further validation rules.</returns>
    /// <remarks>
    /// This method checks if the property's current value matches its previous value stored under the given <paramref name="contextKey"/>
    /// in the validation context. If no previous value is found, the rule passes by default.
    /// </remarks>
    public static IRuleBuilderOptions<T, TProperty> MustNotChange<T, TProperty>(
        this IRuleBuilder<T, TProperty> ruleBuilder,
        string contextKey,
        string message = "'{PropertyName}' is not allowed to change."
    )
        where TProperty : notnull
    {
        return ruleBuilder
            .Must(
                (_, value, context) =>
                    !context.RootContextData.TryGetValue(contextKey, out var previousValue)
                    || value.Equals(previousValue)
            )
            .WithMessage(message);
    }
}

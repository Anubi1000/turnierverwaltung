using System.Reflection;
using System.Text.Json.Serialization;
using Microsoft.AspNetCore.OpenApi;
using Microsoft.OpenApi.Models;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server;

public static partial class Program
{
    private static void AddApiDoc(IServiceCollection services)
    {
        services.AddOpenApi(options =>
        {
            options.AddDocumentTransformer(
                (document, context, cancellationToken) =>
                {
                    document.Info = new OpenApiInfo { Title = "Turnierverwaltung.Server", Version = "1.0.0" };

                    return Task.CompletedTask;
                }
            );

            options.CreateSchemaReferenceId = jsonTypeInfo =>
            {
                var type = jsonTypeInfo.Type;
                if (!type.IsNested)
                    return OpenApiOptions.CreateDefaultSchemaReferenceId(jsonTypeInfo);

                if (type.DeclaringType is not null)
                {
                    var attributes = type.DeclaringType.GetCustomAttributes<JsonDerivedTypeAttribute>();
                    if (attributes.Any())
                        return OpenApiOptions.CreateDefaultSchemaReferenceId(jsonTypeInfo);
                }

                return GetFullName(type);

                string GetFullName(Type type)
                {
                    if (type.DeclaringType == null)
                        return type.Name;
                    return GetFullName(type.DeclaringType) + "_" + type.Name;
                }
            };

            options.AddSchemaTransformer(
                (schema, context, cancellationToken) =>
                {
                    if (
                        context.JsonTypeInfo.Type == typeof(DateTime)
                        && context.JsonTypeInfo.Converter is DateTimeToTimestampConverter
                    )
                    {
                        schema.Format = "int64";
                        schema.Type = "integer";
                    }

                    return Task.CompletedTask;
                }
            );
        });
    }
}

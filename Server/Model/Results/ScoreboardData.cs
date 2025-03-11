using System.Collections.Immutable;
using System.Text.Json.Serialization;

namespace Turnierverwaltung.Server.Model.Results;

public record ScoreboardData(string TournamentName, ImmutableList<ScoreboardData.Table> Tables)
{
    public record Table(string Name, ImmutableList<Table.Column> Columns, ImmutableList<Table.Row> Rows)
    {
        public record Column(
            string Name,
            [property: JsonPropertyName("width")] Column.Width ColumnWidth,
            [property: JsonPropertyName("alignment")] Column.Alignment ColumnAlignment
        )
        {
            [JsonConverter(typeof(JsonStringEnumConverter))]
            public enum Alignment
            {
                Left,
                Center,
                Right,
            }

            [JsonDerivedType(typeof(Fixed))]
            [JsonDerivedType(typeof(Variable))]
            public abstract record Width
            {
                public record Fixed([property: JsonPropertyName("width")] int WidthValue) : Width;

                public record Variable([property: JsonPropertyName("weight")] float Weight) : Width;
            }
        }

        public record Row(int Id, ImmutableList<string> Values);
    }
}

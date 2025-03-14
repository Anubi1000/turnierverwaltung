using System.Collections.Immutable;
using System.Text.Json.Serialization;

namespace Turnierverwaltung.Server.Results.Scoreboard;

public record ScoreboardData(string TournamentName, ImmutableList<ScoreboardData.Table> Tables)
{
    public record Table(string Name, ImmutableList<Table.Column> Columns, ImmutableList<Table.Row> Rows)
    {
        public record Column(
            string Name,
            [property: JsonPropertyName("width")] Column.IWidth ColumnWidth,
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
            public interface IWidth
            {
                public record Fixed([property: JsonPropertyName("width")] int WidthValue) : IWidth;

                public record Variable([property: JsonPropertyName("weight")] float Weight) : IWidth;
            }
        }

        public record Row(int Id, ImmutableList<string> Values);
    }
}

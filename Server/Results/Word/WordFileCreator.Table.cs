using DocumentFormat.OpenXml.Wordprocessing;
using Turnierverwaltung.Server.Results.Scoreboard;

namespace Turnierverwaltung.Server.Results.Word;

public partial class WordFileCreator
{
    /// <summary>
    ///     Adds a table to the Word document based on the provided scoreboard table.
    /// </summary>
    /// <param name="scoreTable">The scoreboard table containing columns and rows.</param>
    private void AddTable(ScoreboardData.Table scoreTable)
    {
        var table = new Table();

        table.AppendChild(
            new TableProperties(
                new TableStyle { Val = ScoreTableStyle },
                new TableWidth { Width = MaxTableWidth.ToString(), Type = TableWidthUnitValues.Pct }
            )
        );
        table.AppendChild(GetTableGridForColumns(scoreTable.Columns));
        table.AppendChild(CreateTableHeader(scoreTable.Columns));

        for (var index = 0; index < scoreTable.Rows.Count; index++)
        {
            var row = scoreTable.Rows[index];
            table.AppendChild(CreateTableResultRow(row, scoreTable.Columns, index));
        }

        _body.AppendChild(table);
    }

    /// <summary>
    ///     Adds a table style to the Word document, defining font, alignment, and borders.
    /// </summary>
    private void AddTableStyle()
    {
        var tableStyle = new Style
        {
            Type = StyleValues.Table,
            StyleId = ScoreTableStyle,

            // Table
            StyleTableProperties = new StyleTableProperties
            {
                TableStyleRowBandSize = new TableStyleRowBandSize { Val = 1 },
                TableBorders = new TableBorders
                {
                    LeftBorder = new LeftBorder { Val = BorderValues.Single, Size = TableBorderSize },
                    TopBorder = new TopBorder { Val = BorderValues.Single, Size = TableBorderSize },
                    RightBorder = new RightBorder { Val = BorderValues.Single, Size = TableBorderSize },
                    BottomBorder = new BottomBorder { Val = BorderValues.Single, Size = TableBorderSize },
                    InsideHorizontalBorder = new InsideHorizontalBorder
                    {
                        Val = BorderValues.Single,
                        Size = TableBorderSize,
                    },
                    InsideVerticalBorder = new InsideVerticalBorder { Val = BorderValues.None },
                },
                TableCellMarginDefault = new TableCellMarginDefault
                {
                    StartMargin = new StartMargin { Width = TableCellMargin },
                    TopMargin = new TopMargin { Width = TableCellMargin },
                    EndMargin = new EndMargin { Width = TableCellMargin },
                    BottomMargin = new BottomMargin { Width = TableCellMargin },
                },
            },

            // Cell
            StyleTableCellProperties = new StyleTableCellProperties
            {
                TableCellVerticalAlignment = new TableCellVerticalAlignment
                {
                    Val = TableVerticalAlignmentValues.Center,
                },
            },

            // Paragraph
            StyleParagraphProperties = new StyleParagraphProperties
            {
                SpacingBetweenLines = new SpacingBetweenLines { After = "0" },
            },

            // Run
            StyleRunProperties = new StyleRunProperties
            {
                Bold = new Bold(),
                FontSize = new FontSize { Val = "24" },
                RunFonts = new RunFonts { Ascii = MainFont },
            },
        };

        // Header style
        tableStyle.AppendChild(
            new TableStyleProperties
            {
                Type = TableStyleOverrideValues.FirstRow,
                TableStyleConditionalFormattingTableCellProperties =
                    new TableStyleConditionalFormattingTableCellProperties
                    {
                        Shading = new Shading { Val = ShadingPatternValues.Clear, Fill = MainColor },
                    },
                RunPropertiesBaseStyle = new RunPropertiesBaseStyle { Color = new Color { Val = "FFFFFF" } },
            }
        );

        // Alternating colors
        tableStyle.AppendChild(
            new TableStyleProperties
            {
                Type = TableStyleOverrideValues.Band1Horizontal,
                TableStyleConditionalFormattingTableCellProperties =
                    new TableStyleConditionalFormattingTableCellProperties
                    {
                        Shading = new Shading { Val = ShadingPatternValues.Clear, Fill = AlternateColor },
                    },
            }
        );

        _styles.AppendChild(tableStyle);
    }

    /// <summary>
    ///     Creates a table header row based on the provided columns.
    /// </summary>
    /// <param name="columns">The list of columns for the table.</param>
    /// <returns>A TableRow representing the header.</returns>
    private static TableRow CreateTableHeader(IList<ScoreboardData.Table.Column> columns)
    {
        var row = new TableRow();

        foreach (var column in columns)
        {
            var cell = new TableCell();

            cell.AppendChild(
                new Paragraph(
                    new ParagraphProperties { Justification = GetJustification(column.ColumnAlignment) },
                    new Run(new Text(column.Name))
                )
            );

            row.AppendChild(cell);
        }

        return row;
    }

    /// <summary>
    ///     Creates a table row with the results for the specified scoreboard row.
    /// </summary>
    /// <param name="row">The row data.</param>
    /// <param name="columns">The column definitions.</param>
    /// <param name="rowIndex">The index of the row in the table.</param>
    /// <returns>A TableRow containing the result values.</returns>
    private static TableRow CreateTableResultRow(
        ScoreboardData.Table.Row row,
        IList<ScoreboardData.Table.Column> columns,
        int rowIndex
    )
    {
        var tableRow = new TableRow();

        var cellColor = rowIndex switch
        {
            0 => FirstPlaceColor,
            1 => SecondPlaceColor,
            2 => ThirdPlaceColor,
            _ => "",
        };

        for (var index = 0; index < row.Values.Count; index++)
        {
            var rowValue = row.Values[index];
            var column = columns[index];

            var cell = new TableCell();

            cell.AppendChild(
                new Paragraph(
                    new ParagraphProperties { Justification = GetJustification(column.ColumnAlignment) },
                    new Run(new Text(rowValue))
                )
            );

            if (rowIndex is >= 0 and <= 2)
                cell.AppendChild(
                    new TableCellProperties
                    {
                        Shading = new Shading { Val = ShadingPatternValues.Clear, Fill = cellColor },
                    }
                );

            tableRow.AppendChild(cell);
        }

        return tableRow;
    }

    /// <summary>
    ///     Generates a TableGrid with appropriate column widths.
    /// </summary>
    /// <param name="columns">The list of columns for the table.</param>
    /// <returns>A TableGrid element for the table.</returns>
    private static TableGrid GetTableGridForColumns(IList<ScoreboardData.Table.Column> columns)
    {
        var totalWeight = 0f;
        var totalFixedWidth = 0;

        foreach (var column in columns)
            switch (column.ColumnWidth)
            {
                case ScoreboardData.Table.Column.IWidth.Variable variableWidth:
                    totalWeight += variableWidth.Weight;
                    break;

                case ScoreboardData.Table.Column.IWidth.Fixed fixedWidth:
                    totalFixedWidth += fixedWidth.WidthValue;
                    break;
            }

        var remainingWidth = MaxTableWidth - totalFixedWidth * PixelConversionFactor;

        var tableColumns = columns.Select(column =>
        {
            var width = column.ColumnWidth switch
            {
                ScoreboardData.Table.Column.IWidth.Fixed fixedWidth => (int)
                    Math.Round(fixedWidth.WidthValue * PixelConversionFactor, MidpointRounding.ToZero),
                ScoreboardData.Table.Column.IWidth.Variable variableWidth => (int)
                    Math.Round(variableWidth.Weight / totalWeight * remainingWidth, MidpointRounding.ToZero),
                _ => throw new InvalidOperationException("Unknown column width type."),
            };

            return new GridColumn { Width = width.ToString() };
        });

        return new TableGrid(tableColumns);
    }
}

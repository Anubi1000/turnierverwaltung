using DocumentFormat.OpenXml;
using DocumentFormat.OpenXml.Packaging;
using DocumentFormat.OpenXml.Wordprocessing;
using Turnierverwaltung.Server.Results.Scoreboard;

namespace Turnierverwaltung.Server.Results.Word;

public class WordFileCreator
{
    private const string MainColor = "1b5e20";
    private const string MainFont = "Aptos";

    private const int A4Width = 842 * 20;
    private const int A4Height = 595 * 20;
    private const int A4Margin = 720;

    private const int TableWidth = 15388;
    private const float PixelToEmuFactor = 7;

    private readonly MemoryStream _stream = new();
    private readonly WordprocessingDocument _wordDocument;
    private readonly MainDocumentPart _mainPart;
    private readonly Body _body;

    private WordFileCreator()
    {
        _wordDocument = WordprocessingDocument.Create(_stream, WordprocessingDocumentType.Document);

        _mainPart = _wordDocument.AddMainDocumentPart();
        _mainPart.Document = new Document();

        _body = _mainPart.Document.AppendChild(new Body());
    }

    public static byte[] CreateWordScoreFileForTable(ScoreboardData scoreboardData, int tableIndex)
    {
        throw new NotImplementedException("Not implemented");
    }

    public static byte[] CreateWordScoreFile(ScoreboardData scoreboardData)
    {
        var creator = new WordFileCreator();
        creator.SetPageSize();

        for (var index = 0; index < scoreboardData.Tables.Count; index++)
        {
            var table = scoreboardData.Tables[index];
            creator.AddDisciplineHeader(scoreboardData.TournamentName, table.Name);

            creator.AddTable(table);

            if (index != scoreboardData.Tables.Count - 1)
            {
                creator._body.Append(new Paragraph(new Run(new Break { Type = BreakValues.Page })));
            }
        }

        return creator.GetDocumentData();
    }

    private void SetPageSize()
    {
        var size = new PageSize
        {
            Orient = PageOrientationValues.Landscape,
            Height = A4Height,
            Width = A4Width,
        };

        var margins = new PageMargin
        {
            Left = A4Margin,
            Top = A4Margin,
            Right = A4Margin,
            Bottom = A4Margin,
        };

        var sectionProperties = new SectionProperties(size, margins);
        _body.AppendChild(sectionProperties);
    }

    private void AddDisciplineHeader(string tournamentName, string disciplineName)
    {
        var paragraph = new Paragraph
        {
            ParagraphProperties = new ParagraphProperties
            {
                Justification = new Justification { Val = JustificationValues.Center },
            },
        };

        paragraph.AppendChild(CreateHeadlineRun(tournamentName, "52"));
        paragraph.AppendChild(CreateHeadlineRun("Der Verein", "40"));
        paragraph.AppendChild(CreateHeadlineRun(disciplineName, "32"));

        _body.AppendChild(paragraph);
    }

    private void AddTable(ScoreboardData.Table scoreTable)
    {
        var columnWidths = CalculateColumnWidths(scoreTable.Columns);

        var tableProperties = new TableProperties(
            new TableWidth { Width = TableWidth.ToString(), Type = TableWidthUnitValues.Dxa },
            new TableBorders
            {
                LeftBorder = new LeftBorder { Val = BorderValues.Single },
                TopBorder = new TopBorder { Val = BorderValues.Single },
                RightBorder = new RightBorder { Val = BorderValues.Single },
                BottomBorder = new BottomBorder { Val = BorderValues.Single },
                InsideHorizontalBorder = new InsideHorizontalBorder { Val = BorderValues.Single },
                InsideVerticalBorder = new InsideVerticalBorder { Val = BorderValues.Single },
            },
            new TableCellMargin
            {
                LeftMargin = new LeftMargin { Width = "80" },
                TopMargin = new TopMargin { Width = "60" },
                RightMargin = new RightMargin { Width = "60" },
                BottomMargin = new BottomMargin { Width = "60" },
            }
        );

        var table = new Table(tableProperties);
        table.AddChild(CreateTableHeader(scoreTable.Columns, columnWidths));

        _body.AppendChild(table);
    }

    private byte[] GetDocumentData()
    {
        _mainPart.Document.Save();
        _wordDocument.Save();
        _wordDocument.Dispose();

        var data = _stream.ToArray();
        _stream.Dispose();

        return data;
    }

    private static Run CreateHeadlineRun(string text, string fontSize)
    {
        var properties = new RunProperties(
            new Bold(),
            new FontSize { Val = fontSize },
            new Color { Val = MainColor },
            new RunFonts { Ascii = MainFont }
        );

        var run = new Run(properties, new Text(text), new Break());

        return run;
    }

    private static TableRow CreateTableHeader(IList<ScoreboardData.Table.Column> columns, int[] columnWidths)
    {
        var row = new TableRow();

        for (var index = 0; index < columns.Count; index++)
        {
            var column = columns[index];

            var paragraph = new Paragraph(
                new ParagraphProperties
                {
                    SpacingBetweenLines = new SpacingBetweenLines { After = "0" },
                    Justification = GetJustification(column.ColumnAlignment),
                },
                new Run(
                    new RunProperties(
                        new Bold(),
                        new FontSize { Val = "24" },
                        new Color { Val = "FFFFFF" },
                        new RunFonts { Ascii = MainFont }
                    ),
                    new Text(column.Name)
                )
            );

            var cellProperties = new TableCellProperties
            {
                TableCellWidth = new TableCellWidth
                {
                    Width = columnWidths[index].ToString(),
                    Type = TableWidthUnitValues.Dxa,
                },
                TableCellVerticalAlignment = new TableCellVerticalAlignment
                {
                    Val = TableVerticalAlignmentValues.Center,
                },
                Shading = new Shading { Val = ShadingPatternValues.Clear, Fill = MainColor },
                TableCellBorders = new TableCellBorders
                {
                    TopBorder = new TopBorder { Val = BorderValues.Single },
                    BottomBorder = new BottomBorder { Val = BorderValues.Single },
                    LeftBorder =
                        index == 0
                            ? new LeftBorder { Val = BorderValues.Single }
                            : new LeftBorder { Val = BorderValues.Nil },
                    RightBorder =
                        index == columns.Count - 1
                            ? new RightBorder { Val = BorderValues.Single }
                            : new RightBorder { Val = BorderValues.Nil },
                }
            };

            var cell = new TableCell(cellProperties, paragraph);

            row.AppendChild(cell);
        }

        return row;
    }

    private static int[] CalculateColumnWidths(IList<ScoreboardData.Table.Column> columns)
    {
        var totalWeight = 0f;
        var totalFixedWidth = 0;

        foreach (var column in columns)
        {
            switch (column.ColumnWidth)
            {
                case ScoreboardData.Table.Column.Width.Variable variableWidth:
                    totalWeight += variableWidth.Weight;
                    break;

                case ScoreboardData.Table.Column.Width.Fixed fixedWidth:
                    totalFixedWidth += fixedWidth.WidthValue;
                    break;
            }
        }

        var remainingWidth = TableWidth - totalFixedWidth * PixelToEmuFactor;

        return columns
            .Select(column =>
            {
                return column.ColumnWidth switch
                {
                    ScoreboardData.Table.Column.Width.Fixed fixedWidth => (int)
                        Math.Round(fixedWidth.WidthValue * PixelToEmuFactor),
                    ScoreboardData.Table.Column.Width.Variable variableWidth => (int)
                        Math.Round(variableWidth.Weight / totalWeight * remainingWidth),
                    _ => throw new InvalidOperationException("Unknown column width type."),
                };
            })
            .ToArray();
    }

    private static Justification GetJustification(ScoreboardData.Table.Column.Alignment alignment)
    {
        var value = alignment switch
        {
            ScoreboardData.Table.Column.Alignment.Left => JustificationValues.Left,
            ScoreboardData.Table.Column.Alignment.Center => JustificationValues.Center,
            ScoreboardData.Table.Column.Alignment.Right => JustificationValues.Right,
            _ => throw new ArgumentOutOfRangeException(nameof(alignment))
        };

        return new Justification { Val = value };
    }
}

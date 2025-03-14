using DocumentFormat.OpenXml;
using DocumentFormat.OpenXml.Packaging;
using DocumentFormat.OpenXml.Wordprocessing;
using Turnierverwaltung.Server.Results.Scoreboard;

namespace Turnierverwaltung.Server.Results.Word;

/// <summary>
///     Class responsible for creating Word documents containing scoreboard data.
/// </summary>
public partial class WordFileCreator
{
    private const string MainColor = "1b5e20";
    private const string MainFont = "Aptos";
    private const string AlternateColor = "b9dabb";
    private const string FirstPlaceColor = "ffd700";
    private const string SecondPlaceColor = "c0c0c0";
    private const string ThirdPlaceColor = "bf8970";

    private const string DisciplineHeadlineStyle = "DisciplineHeadline";
    private const string ScoreTableStyle = "ScoreTable";

    private const int A4Width = 16838;
    private const int A4Height = 11906;
    private const int A4Margin = 720;

    private const int MaxTableWidth = 5000;
    private const float PixelConversionFactor = 2.75f;
    private readonly Body _body;
    private readonly MainDocumentPart _mainPart;

    private readonly MemoryStream _stream = new();
    private readonly Styles _styles;
    private readonly WordprocessingDocument _wordDocument;

    /// <summary>
    ///     Initializes a new instance of the <see cref="WordFileCreator" /> class.
    /// </summary>
    private WordFileCreator()
    {
        _wordDocument = WordprocessingDocument.Create(_stream, WordprocessingDocumentType.Document);

        _mainPart = _wordDocument.AddMainDocumentPart();
        _mainPart.Document = new Document();

        var stylePart = _mainPart.AddNewPart<StyleDefinitionsPart>();
        stylePart.Styles = new Styles();
        _styles = stylePart.Styles;

        _body = _mainPart.Document.AppendChild(new Body());
    }

    /// <summary>
    ///     Creates a Word document for a specific scoreboard table.
    /// </summary>
    /// <param name="scoreboardData">The scoreboard data containing tables.</param>
    /// <param name="tableIndex">The index of the table to generate.</param>
    /// <returns>A byte array representing the Word document.</returns>
    public static byte[] CreateWordScoreFileForTable(ScoreboardData scoreboardData, int tableIndex)
    {
        var creator = new WordFileCreator();
        creator.SetPageSize();
        creator.AddTableHeadingStyle();
        creator.AddTableStyle();

        var table = scoreboardData.Tables[tableIndex];
        creator.AddTableHeading(scoreboardData.TournamentName, table.Name);
        creator.AddTable(table);

        return creator.GetDocumentData();
    }

    /// <summary>
    ///     Creates a Word document containing all scoreboard tables.
    /// </summary>
    /// <param name="scoreboardData">The scoreboard data containing multiple tables.</param>
    /// <returns>A byte array representing the Word document.</returns>
    public static byte[] CreateWordScoreFile(ScoreboardData scoreboardData)
    {
        var creator = new WordFileCreator();
        creator.SetPageSize();
        creator.AddTableHeadingStyle();
        creator.AddTableStyle();

        for (var index = 0; index < scoreboardData.Tables.Count; index++)
        {
            var table = scoreboardData.Tables[index];
            creator.AddTableHeading(scoreboardData.TournamentName, table.Name);
            creator.AddTable(table);

            if (index != scoreboardData.Tables.Count - 1)
                creator._body.Append(new Paragraph(new Run(new Break { Type = BreakValues.Page })));
        }

        return creator.GetDocumentData();
    }

    /// <summary>
    ///     Sets the page size and margins for the Word document.
    /// </summary>
    private void SetPageSize()
    {
        var sectionProperties = new SectionProperties();
        sectionProperties.AppendChild(
            new PageSize
            {
                Orient = PageOrientationValues.Landscape,
                Height = A4Height,
                Width = A4Width,
            }
        );

        sectionProperties.AppendChild(
            new PageMargin
            {
                Left = A4Margin,
                Top = A4Margin,
                Right = A4Margin,
                Bottom = A4Margin,
                Header = A4Margin,
                Footer = A4Margin,
                Gutter = 0,
            }
        );

        _body.AppendChild(sectionProperties);
    }

    /// <summary>
    ///     Adds a table header to the document.
    /// </summary>
    /// <param name="tournamentName">The name of the tournament.</param>
    /// <param name="disciplineName">The name of the discipline.</param>
    private void AddTableHeading(string tournamentName, string disciplineName)
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
        paragraph.AppendChild(CreateHeadlineRun(disciplineName, "32", false));

        _body.AppendChild(paragraph);
    }

    /// <summary>
    ///     Adds a table header style to the document.
    /// </summary>
    private void AddTableHeadingStyle()
    {
        var style = new Style
        {
            Type = StyleValues.Character,
            StyleId = DisciplineHeadlineStyle,
            StyleRunProperties = new StyleRunProperties
            {
                Bold = new Bold(),
                Color = new Color { Val = MainColor },
                RunFonts = new RunFonts { Ascii = MainFont },
            },
        };

        _styles.AppendChild(style);
    }

    /// <summary>
    ///     Finalizes and retrieves the Word document data as a byte array.
    /// </summary>
    /// <returns>The byte array of the document.</returns>
    private byte[] GetDocumentData()
    {
        _mainPart.Document.Save();
        _wordDocument.Save();
        _wordDocument.Dispose();

        var data = _stream.ToArray();
        _stream.Dispose();

        return data;
    }

    /// <summary>
    ///     Creates a formatted run for a headline.
    /// </summary>
    /// <param name="text">The text to display.</param>
    /// <param name="fontSize">The font size of the text.</param>
    /// <param name="addBreak">Whether to add a line break after the text.</param>
    /// <returns>A Run element with the specified formatting.</returns>
    private static Run CreateHeadlineRun(string text, string fontSize, bool addBreak = true)
    {
        var run = new Run();
        run.AppendChild(
            new RunProperties
            {
                RunStyle = new RunStyle { Val = DisciplineHeadlineStyle },
                FontSize = new FontSize { Val = fontSize },
            }
        );
        run.AppendChild(new Text(text));

        if (addBreak)
            run.AppendChild(new Break());

        return run;
    }

    /// <summary>
    ///     Gets the appropriate justification for a given text alignment.
    /// </summary>
    /// <param name="alignment">The text alignment.</param>
    /// <returns>A Justification object representing the alignment.</returns>
    private static Justification GetJustification(ScoreboardData.Table.Column.Alignment alignment)
    {
        var value = alignment switch
        {
            ScoreboardData.Table.Column.Alignment.Left => JustificationValues.Left,
            ScoreboardData.Table.Column.Alignment.Center => JustificationValues.Center,
            ScoreboardData.Table.Column.Alignment.Right => JustificationValues.Right,
            _ => throw new ArgumentOutOfRangeException(nameof(alignment), alignment, null),
        };

        return new Justification { Val = value };
    }
}

using DocumentFormat.OpenXml;
using DocumentFormat.OpenXml.Packaging;
using DocumentFormat.OpenXml.Wordprocessing;
using Turnierverwaltung.Server.Config;
using Turnierverwaltung.Server.Results.Scoreboard;
using Turnierverwaltung.Server.Utils;

namespace Turnierverwaltung.Server.Results.Word;

public partial class WordFileCreator(UserDataService userDataService, AppConfig appConfig) : IWordFileCreator
{
    private const string MainColor = "1b5e20";
    private const string MainFont = "Aptos";
    private const string AlternateColor = "b9dabb";
    private const string FirstPlaceColor = "ffd700";
    private const string SecondPlaceColor = "c0c0c0";
    private const string ThirdPlaceColor = "bf8970";

    private const int A4Width = 16838;
    private const int A4Height = 11906;
    private const int A4Margin = 720;

    /// <summary>
    ///     Creates a Word document as a memory stream containing all tables from the scoreboard data.
    /// </summary>
    /// <param name="scoreboardData">The scoreboard data to be included in the document.</param>
    /// <returns>A memory stream containing the generated Word document.</returns>
    public async Task<MemoryStream> CreateWordFileAsStream(ScoreboardData scoreboardData)
    {
        var stream = new MemoryStream();
        using (var document = WordprocessingDocument.Create(stream, WordprocessingDocumentType.Document))
        {
            // Add main part
            var mainPart = document.AddMainDocumentPart();
            mainPart.Document = new Document();

            // Add logo
            var logoPartId = await AddLogoImagePart(mainPart);

            // Add styles
            AddStyles(mainPart);

            // Add body
            var body = mainPart.Document.AppendChild(new Body());

            for (var index = 0; index < scoreboardData.Tables.Count; index++)
            {
                var table = scoreboardData.Tables[index];
                body.AppendChild(CreateTableHeading(scoreboardData.TournamentName, table.Name, logoPartId));
                body.AppendChild(CreateScoreTable(table));

                var footerPartId = AddFooterPart(mainPart, scoreboardData.TournamentName, table.Name);

                if (index != scoreboardData.Tables.Count - 1)
                    body.AppendChild(
                        new Paragraph(
                            new ParagraphProperties { SectionProperties = GetSectionProperties(footerPartId) },
                            new Run(new Break { Type = BreakValues.Page })
                        )
                    );
                else
                    body.AppendChild(GetSectionProperties(footerPartId));
            }

            mainPart.Document.Save();
            document.Save();
        }

        stream.Seek(0, SeekOrigin.Begin);
        return stream;
    }

    /// <summary>
    ///     Creates a Word document as a memory stream containing a single scoreboard table.
    /// </summary>
    /// <param name="scoreboardData">The scoreboard data containing multiple tables.</param>
    /// <param name="tableIndex">The index of the table to include in the document.</param>
    /// <returns>A memory stream containing the generated Word document.</returns>
    public async Task<MemoryStream> CreateWordFileForTableAsStream(ScoreboardData scoreboardData, int tableIndex)
    {
        var stream = new MemoryStream();
        using (var document = WordprocessingDocument.Create(stream, WordprocessingDocumentType.Document))
        {
            // Add main part
            var mainPart = document.AddMainDocumentPart();
            mainPart.Document = new Document();

            // Add logo
            var logoPartId = await AddLogoImagePart(mainPart);

            // Add styles
            AddStyles(mainPart);

            // Add body
            var body = mainPart.Document.AppendChild(new Body());

            var table = scoreboardData.Tables[tableIndex];
            body.AppendChild(CreateTableHeading(scoreboardData.TournamentName, table.Name, logoPartId));
            body.AppendChild(CreateScoreTable(table));

            var footerPartId = AddFooterPart(mainPart, scoreboardData.TournamentName, table.Name);
            body.AppendChild(GetSectionProperties(footerPartId));

            mainPart.Document.Save();
            document.Save();
        }

        stream.Seek(0, SeekOrigin.Begin);
        return stream;
    }

    /// <summary>
    ///     Adds styles to the Word document.
    /// </summary>
    /// <param name="mainPart">The main document part.</param>
    private static void AddStyles(MainDocumentPart mainPart)
    {
        var stylePart = mainPart.AddNewPart<StyleDefinitionsPart>();
        stylePart.Styles = new Styles();
        var styles = stylePart.Styles;

        styles.AppendChild(CreateTableStyle());

        styles.Save();
    }

    /// <summary>
    ///     Creates section properties with a footer reference and page setup.
    /// </summary>
    /// <param name="footerPartId">The ID of the footer part.</param>
    /// <returns>A SectionProperties object for the document.</returns>
    private static SectionProperties GetSectionProperties(string footerPartId)
    {
        var sectionProperties = new SectionProperties(
            new FooterReference { Type = HeaderFooterValues.Default, Id = footerPartId },
            new PageNumberType { Start = 1 },
            new PageSize
            {
                Orient = PageOrientationValues.Landscape,
                Height = A4Height,
                Width = A4Width,
            },
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

        return sectionProperties;
    }

    /// <summary>
    ///     Adds a footer part to the document.
    /// </summary>
    /// <param name="mainPart">The main document part.</param>
    /// <param name="tournamentName">The tournament name.</param>
    /// <param name="tableName">The table name.</param>
    /// <returns>The ID of the footer part.</returns>
    private static string AddFooterPart(MainDocumentPart mainPart, string tournamentName, string tableName)
    {
        var footerPart = mainPart.AddNewPart<FooterPart>();
        footerPart.Footer = new Footer(
            new Paragraph(
                // Tournament name
                new Run(new Text(tournamentName)),
                // Table name
                new Run(
                    new PositionalTab
                    {
                        RelativeTo = AbsolutePositionTabPositioningBaseValues.Margin,
                        Alignment = AbsolutePositionTabAlignmentValues.Center,
                        Leader = AbsolutePositionTabLeaderCharValues.None,
                    }
                ),
                new Run(new Text(tableName)),
                // Page
                new Run(
                    new PositionalTab
                    {
                        RelativeTo = AbsolutePositionTabPositioningBaseValues.Margin,
                        Alignment = AbsolutePositionTabAlignmentValues.Right,
                        Leader = AbsolutePositionTabLeaderCharValues.None,
                    }
                ),
                new SimpleField { Instruction = "PAGE" },
                new Run(new Text(" / ") { Space = SpaceProcessingModeValues.Preserve }),
                new SimpleField { Instruction = "SECTIONPAGES" }
            )
        );
        footerPart.Footer.Save();

        return mainPart.GetIdOfPart(footerPart);
    }

    /// <summary>
    ///     Creates a paragraph for the table heading, including tournament name, discipline name, and logo.
    /// </summary>
    /// <param name="tournamentName">The name of the tournament.</param>
    /// <param name="disciplineName">The name of the discipline.</param>
    /// <param name="logoPartId">The ID of the logo part, if available.</param>
    /// <returns>A formatted Paragraph element.</returns>
    private Paragraph CreateTableHeading(string tournamentName, string disciplineName, string? logoPartId)
    {
        var paragraph = new Paragraph
        {
            ParagraphProperties = new ParagraphProperties
            {
                Justification = new Justification { Val = JustificationValues.Center },
            },
        };

        paragraph.AppendChild(CreateHeadlineRun(tournamentName, "52"));
        paragraph.AppendChild(CreateHeadlineRun(appConfig.ClubName!, "40"));
        paragraph.AppendChild(CreateHeadlineRun(disciplineName, "32", false));
        if (logoPartId is not null)
            paragraph.AppendChild(new Run(CreateLogoDrawing(logoPartId)));

        return paragraph;
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
                Bold = new Bold(),
                Color = new Color { Val = MainColor },
                RunFonts = new RunFonts { Ascii = MainFont },
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

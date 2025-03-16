using DocumentFormat.OpenXml.Drawing;
using DocumentFormat.OpenXml.Packaging;
using DocumentFormat.OpenXml.Wordprocessing;
using DPictures = DocumentFormat.OpenXml.Drawing.Pictures;
using DWp = DocumentFormat.OpenXml.Drawing.Wordprocessing;

namespace Turnierverwaltung.Server.Results.Word;

public partial class WordFileCreator
{
    private const int LogoSize = 1100000;

    private static Drawing CreateLogoDrawing(string logoPartId)
    {
        return new Drawing(
            new DWp.Anchor(
                new DWp.SimplePosition { X = 0, Y = 0 },
                new DWp.HorizontalPosition
                {
                    RelativeFrom = DWp.HorizontalRelativePositionValues.Column,
                    PositionOffset = new DWp.PositionOffset("-1"),
                },
                new DWp.VerticalPosition
                {
                    RelativeFrom = DWp.VerticalRelativePositionValues.Paragraph,
                    PositionOffset = new DWp.PositionOffset("0"),
                },
                new DWp.Extent { Cx = LogoSize, Cy = LogoSize },
                new DWp.WrapNone(),
                new DWp.DocProperties { Id = 1, Name = "Logo" },
                new DWp.NonVisualGraphicFrameDrawingProperties
                {
                    GraphicFrameLocks = new GraphicFrameLocks { NoChangeAspect = true },
                },
                new Graphic(
                    new GraphicData(
                        new DPictures.Picture(
                            new DPictures.NonVisualPictureProperties
                            {
                                NonVisualDrawingProperties = new DPictures.NonVisualDrawingProperties
                                {
                                    Id = 1,
                                    Name = "Logo",
                                },
                                NonVisualPictureDrawingProperties = new DPictures.NonVisualPictureDrawingProperties(),
                            },
                            new DPictures.BlipFill(
                                new Blip { Embed = logoPartId, CompressionState = BlipCompressionValues.Print },
                                new Stretch { FillRectangle = new FillRectangle() }
                            ),
                            new DPictures.ShapeProperties(
                                new Transform2D
                                {
                                    Offset = new Offset { X = 0, Y = 0 },
                                    Extents = new Extents { Cx = LogoSize, Cy = LogoSize },
                                },
                                new PresetGeometry
                                {
                                    AdjustValueList = new AdjustValueList(),
                                    Preset = ShapeTypeValues.Rectangle,
                                }
                            )
                        )
                    )
                    {
                        Uri = "http://schemas.openxmlformats.org/drawingml/2006/picture",
                    }
                )
            )
            {
                SimplePos = false,
                AllowOverlap = true,
                BehindDoc = false,
                Locked = false,
                LayoutInCell = true,
                Hidden = false,
                RelativeHeight = 10000,
            }
        );
    }

    private static string? AddLogoImagePart(MainDocumentPart mainPart)
    {
        var logoPath = Program.GetUserData(UserDataType.WordDocumentIcon);
        if (!File.Exists(logoPath))
            return null;

        var imgPart = mainPart.AddImagePart(ImagePartType.Png);
        using (var stream = new FileStream(logoPath, FileMode.Open, FileAccess.Read))
        {
            imgPart.FeedData(stream);
        }

        return mainPart.GetIdOfPart(imgPart);
    }
}

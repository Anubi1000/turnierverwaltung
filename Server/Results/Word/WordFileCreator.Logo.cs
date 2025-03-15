using DocumentFormat.OpenXml.Drawing;
using DocumentFormat.OpenXml.Drawing.Wordprocessing;
using DocumentFormat.OpenXml.Wordprocessing;
using DPictures = DocumentFormat.OpenXml.Drawing.Pictures;
using DWp = DocumentFormat.OpenXml.Drawing.Wordprocessing;

namespace Turnierverwaltung.Server.Results.Word;

public partial class WordFileCreator
{
    private const int LogoSize = 1100000;

    private Drawing CreateLogoDrawing()
    {
        return new Drawing(
            new DWp.Anchor(
                new SimplePosition { X = 0, Y = 0 },
                new HorizontalPosition
                {
                    RelativeFrom = HorizontalRelativePositionValues.Column,
                    PositionOffset = new PositionOffset("-1"),
                },
                new VerticalPosition
                {
                    RelativeFrom = VerticalRelativePositionValues.Paragraph,
                    PositionOffset = new PositionOffset("0"),
                },
                new Extent { Cx = LogoSize, Cy = LogoSize },
                new WrapNone(),
                new DocProperties { Id = 1, Name = "Logo" },
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
                                new Blip { Embed = _logoPartId, CompressionState = BlipCompressionValues.Print },
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
}

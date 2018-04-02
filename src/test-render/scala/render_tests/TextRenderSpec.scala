package render_tests

import java.io.File

import com.ajjpj.adiagram.ADiagramSpec
import com.ajjpj.adiagram.geometry.{Angle, LenUnit, Length, Vector2}
import com.ajjpj.adiagram.render.TextAtomStyle.{UnderlineDouble, UnderlineKind, UnderlineNone, UnderlineSingle}
import com.ajjpj.adiagram.render.{Model2Screen, _}
import com.ajjpj.adiagram.render.text.{RenderableText, TextAtomModel, TextModel, TextParagraphModel}
import javafx.embed.swing.SwingFXUtils
import javafx.scene.canvas.Canvas
import javafx.scene.paint.{Color, Paint}
import javafx.scene.text.TextAlignment
import javax.imageio.ImageIO


class TextRenderSpec extends ADiagramSpec with RenderUtils {
  import LenUnit._

  override def componentId = "text"

  private def render(folderName: String, filename: String, zoom: Double, text: RenderableText): Unit = {
    val folder = new File(baseDir, folderName)
    folder.mkdirs()

    val m2s = new Model2Screen (zoom)
    val riRaw = text.render (m2s)
    val imgRaw = riRaw.img

    val canvas = new Canvas (imgRaw.getWidth, imgRaw.getHeight)
    val gc = canvas.getGraphicsContext2D

    gc.drawImage (imgRaw, 0, 0)
    val img = RenderHelper.snapshot (canvas, Color.WHITE)

    ImageIO.write (SwingFXUtils.fromFXImage (img, null), "png", new File (folder, s"$filename.png"))
  }

  val FONT_FAMILY_TIMES = "Times New Roman"
  val FONT_FAMILY_COURIER = "Courier New"

  "RenderableText" should "format text in the specified style" in {
    for (hAlignment <- TextAlignment.values;
         sizePt <- Vector(10.0, 20.0, 40.0);
         zoom <- Map("full" -> 1.0, "half" -> .5, "double" -> 2.0)
    ) {
      val paragraphs = {
        def style(sizeFactor: Double = 1.0, fontFamily: String = FONT_FAMILY_TIMES, color: Paint = Color.BLACK,
                  italics: Boolean = false, bold: Boolean = false, underline: UnderlineKind = UnderlineNone, strikeThrough: Boolean = false) =
          TextAtomStyle(fontFamily, Length(sizePt * sizeFactor, pt), color, italics, bold, underline, strikeThrough)

        val atoms1 = Vector (
          TextAtomModel("This is ", style()),
          TextAtomModel("BIG", style(sizeFactor = 3)),
          TextAtomModel(" and ", style()),
          TextAtomModel("small", style(sizeFactor=.5)),
          TextAtomModel(" arbitrary text that is long enough to wrap around to several lines.", style())
        )
        val atoms2 = Vector (
          TextAtomModel("It has ", style()),
          TextAtomModel("italics", style(italics=true)),
          TextAtomModel(" and ", style()),
          TextAtomModel("bold text", style(bold=true)),
          TextAtomModel(" and some ", style()),
          TextAtomModel("bold italics", style(bold=true, italics=true)),
          TextAtomModel(". ", style()),
          TextAtomModel("Also in Courier: ", style(fontFamily = FONT_FAMILY_COURIER)),
          TextAtomModel("italics, ", style(fontFamily=FONT_FAMILY_COURIER, italics=true)),
          TextAtomModel("bold, and ", style(fontFamily = FONT_FAMILY_COURIER, bold=true)),
          TextAtomModel("bold italics.", style(fontFamily = FONT_FAMILY_COURIER, bold=true, italics=true))
        )
        val atoms3 = Vector (
          TextAtomModel("There is also ", style()),
          TextAtomModel("strikethrough", style(strikeThrough = true)),
          TextAtomModel(" (big)", style(sizeFactor=2, strikeThrough = true)),
          TextAtomModel(" (small)", style(sizeFactor=.5, strikeThrough = true)),
          TextAtomModel(" and ", style()),
          TextAtomModel("underlined", style(underline = UnderlineSingle)),
          TextAtomModel(" (big)", style(sizeFactor=2, underline = UnderlineSingle)),
          TextAtomModel(" (small)", style(sizeFactor=.5, underline = UnderlineSingle)),
          TextAtomModel(" and ", style()),
          TextAtomModel("double underlined", style(underline = UnderlineDouble)),
          TextAtomModel(" (big)", style(sizeFactor=2, underline = UnderlineDouble)),
          TextAtomModel(" (small)", style(sizeFactor=.5, underline = UnderlineDouble)),
          TextAtomModel(" text. And there is text in ", style()),
          TextAtomModel("blue", style(color = Color.BLUE)),
          TextAtomModel(" and in ", style()),
          TextAtomModel("red", style(color = Color.RED)),
          TextAtomModel(".", style())
        )
        Vector (
          TextParagraphModel(atoms1, TextParagraphStyle(hAlignment)),
          TextParagraphModel(atoms2, TextParagraphStyle(hAlignment)),
          TextParagraphModel(atoms3, TextParagraphStyle(hAlignment))
        )
      }

      render("regular", s"styles-$hAlignment-${sizePt.toInt}pt-${zoom._1}", zoom._2, RenderableText(Vector2.ZERO, Length(330, LenUnit.pt), TextModel(paragraphs, TextStyle()), None))
    }
    //TODO superscript, subscript, caps
  }

  private def atomStyle(fontFamily: String = FONT_FAMILY_TIMES, fontSize: Double = 20, fill: Paint = Color. BLACK) = TextAtomStyle(fontFamily, Length(fontSize, pt), fill)

  it should "respect line and paragraph spacing" in {
    def paragraph(text: String, spacing: Double) = TextParagraphModel (Vector (TextAtomModel(text, atomStyle())), TextParagraphStyle(TextAlignment.JUSTIFY, Length(spacing, pt)))

    for (paragraphSpacing <- Vector(0, 2, 5, 9);
         zoom <- Vector(1, 2)
    ) {
      val paragraphs = Vector (
        paragraph("Normal line spacing: This paragraph has normal line spacing and is included as a reference", 0),
        paragraph("Two points additional line spacing: These lines are a little further apart", 2),
        paragraph("Five points additional line spacing: These lines are further apart", 5),
        paragraph("Ten points additional line spacing: These lines are pretty far apart", 10)
      )
      val text = RenderableText(Vector2.ZERO, Length(200, pt), TextModel(paragraphs, TextStyle(Length(paragraphSpacing, pt))), None)
      render("special", s"spacing-$paragraphSpacing-${zoom}x", zoom, text)
    }
  }

  it should "handle exotic cases for JUSTIFY" in {
    def paragraph(text: String) = TextParagraphModel (Vector (TextAtomModel(text, atomStyle())), TextParagraphStyle(TextAlignment.JUSTIFY))

    // handle two special cases:
    //  * a single word in a line that is too short for justified typesetting, and
    //  * a single word in a line that does not fit
    val paragraphs = Vector(
      paragraph("bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla"),
      paragraph("SingleWord ThisIsAVeryLongWordThatDoesNotFitOnALine and some bla bla bla bla bla bla bla bla bla bla"),
      paragraph("bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla")
    )
    val text = RenderableText(Vector2.ZERO, Length(200, pt), TextModel(paragraphs, TextStyle(Length(10, pt))), None)
    render("special", s"justify-single-word", 1, text)

    //TODO wide word for RIGHT
  }

  it should "render text at an angle" in {
    for (angle <- Vector(0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330);
         hAlign <- Vector(TextAlignment.LEFT, TextAlignment.CENTER, TextAlignment.RIGHT, TextAlignment.JUSTIFY)
    ) {
      val paragraph = TextParagraphModel(Vector(TextAtomModel("This is some arbitrary text that spans several lines", atomStyle())), TextParagraphStyle(hAlign))

      val text = RenderableText(Vector2.ZERO, Length(200, pt), TextModel(Vector(paragraph), TextStyle(angle = Angle(angle * Math.PI / 180))), None)
      render("angle", s"angle-${String.format("%03d", int2Integer(angle))}-$hAlign", 1, text)
//      fail("not yet implemented")
    }
  }
}

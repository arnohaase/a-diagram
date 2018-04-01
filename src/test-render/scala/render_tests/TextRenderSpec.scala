package render_tests

import java.io.File

import com.ajjpj.adiagram.ADiagramSpec
import com.ajjpj.adiagram.geometry.{LenUnit, Length, Vector2}
import com.ajjpj.adiagram.render.TextAtomStyle.{UnderlineDouble, UnderlineKind, UnderlineNone, UnderlineSingle}
import com.ajjpj.adiagram.render._
import com.ajjpj.adiagram.render.text.{RenderableText, TextAtomModel, TextModel, TextParagraphModel}
import javafx.embed.swing.SwingFXUtils
import javafx.scene.canvas.Canvas
import javafx.scene.paint.{Color, Paint}
import javafx.scene.text.TextAlignment
import javax.imageio.ImageIO


class TextRenderSpec extends ADiagramSpec with RenderUtils {
  import LenUnit._

  override def componentId = "text"

  private def doRenderToFile(folder: File, filename: String, text: RenderableText, zoom: Double): Unit = {
    val m2s = new Model2Screen(zoom)
    val riRaw = text.render(m2s)
    val imgRaw = riRaw.img
    val ro = riRaw.topLeftPos

    val canvas = new Canvas(imgRaw.getWidth, imgRaw.getHeight)
    val gc = canvas.getGraphicsContext2D

    gc.drawImage(imgRaw, 0, 0)
    val img = RenderHelper.snapshot(canvas, Color.WHITE)

    ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", new File(folder, s"$filename.png"))
  }

  private def render(folderName: String, filename: String, zoom: Double, text: RenderableText): Unit = {
    val folder = new File(baseDir, folderName)
    folder.mkdirs()

    doRenderToFile(folder, filename, text, zoom)
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

  //TODO line spacing --> two paragraphs for comparison
  //TODO paragraph spacing

  //TODO angle: steps of 30° to take care of all variations
  //TODO single word on a line, word wider than line
}

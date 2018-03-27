package render_tests

import java.io.File
import javafx.embed.swing.SwingFXUtils
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javax.imageio.ImageIO

import com.ajjpj.adiagram.ADiagramSpec
import com.ajjpj.adiagram.geometry._
import com.ajjpj.adiagram.render.lineend._
import com.ajjpj.adiagram.render.{LineStyle, Model2Screen, RenderHelper, RenderableStraightLine}
import com.sun.javafx.application.PlatformImpl


class StrightLineRenderSpec extends ADiagramSpec {
  import LenUnit._

  PlatformImpl.startup(() => {})
  val baseDir = new File("target/render-test/straight-line")
  baseDir.mkdirs()
  delRec(baseDir)

  private def delRec(f: File): Unit = {
    Option(f.listFiles).getOrElse(Array.empty).foreach(delRec)
    if (f.isFile) f.delete()
  }

  private def doRenderToFile(folder: File, filename: String, line: RenderableStraightLine, zoom: Double, coordId: String): Unit = {
    val m2s = new Model2Screen(10 * zoom)
    val piRaw = line.render(m2s)
    val imgRaw = piRaw.shape.img
    val ro = piRaw.shape.renderOffset

    val canvas = new Canvas(imgRaw.getWidth, imgRaw.getHeight)
    val gc = canvas.getGraphicsContext2D
    gc.setStroke(Color.RED)
    gc.setLineWidth(40)

    coordId match {
      case "horizontal" => gc.strokeRect(-m2s(ro.x,pt)-20, -10000, m2s(80,pt)+40, 20000)
      case "diagonal" => gc.strokeRect(-m2s(ro.x,pt)-20, -m2s(ro.y,pt)-20, m2s(80,pt)+40, m2s(80,pt)+40)
      case "vertical" => gc.strokeRect(-10000, -m2s(ro.y,pt)-20, 20000, m2s(80,pt)+40)
    }

    gc.drawImage(imgRaw, 0, 0)
    val img = RenderHelper.snapshot(canvas, Color.WHITE)

    ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", new File(folder, s"$filename.png"))
  }

  private def render(folderName: String, decoration: RenderableLineEnd): Unit = { //TODO 'base' part of file names
    val folder = new File(baseDir, folderName)
    folder.mkdirs()

    val zooms = Map("full" -> 1.0, "half" -> .5, "double" -> 2.0)
    val lineWidths = Vector(.1, .5, 1, 5, 10)
    val coords = Vector("horizontal" -> ((10,10), (90,10)), "diagonal" -> ((10,10), (90,90)), "vertical" -> ((10,10), (10,90)))

    for (coord <- coords;
         lineWidth <- lineWidths;
         zoom <- zooms
         ) {
      val m2s = new Model2Screen(zoom._2)
      val style = LineStyle(Length(lineWidth, pt), Color.BLACK)

      val pp = coord._2
      val p0 = Vector2(pp._1._1, pp._1._2, pt)
      val p1 = Vector2(pp._2._1, pp._2._2, pt)

      val line = RenderableStraightLine(p0, p1, style, decoration, decoration)

      val formattedLineWidth = String.format("%03d", int2Integer((lineWidth*10).toInt))
      val filename = s"${coord._1}-$formattedLineWidth-${zoom._1}"
      doRenderToFile(folder, filename, line, zoom._2, coord._1)
    }
  }

  "RenderableStraightLine" should "render with NullLineEnd" in {
    render("null-lineend", new NullLineEnd)
  }

  it should "render with PointedArrowLineEnd" in {
    render("pointed-arrow", new PointedArrowLineEnd)
  }

  it should "render with RoundArrowLineEnd" in {
    render("rounded-arrow", new RoundArrowLineEnd)
  }

  it should "render with RoundedCornerLineEnd" in {
    render("rounded-corner", new RoundedCornerLineEnd)
  }

  it should "render with RoundPointedArrowLineEnd" in {
    render("round-pointed-arrow", new RoundPointedArrowLineEnd)
  }

  it should "render with SemiCircleLineEnd" in {
    render("semi-circle", new SemiCircleLineEnd)
  }
}

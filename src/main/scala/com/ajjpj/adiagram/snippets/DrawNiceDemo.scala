package com.ajjpj.adiagram.snippets

import javafx.stage.Stage
import javafx.scene.layout.StackPane
import javafx.scene.Scene
import javafx.scene.canvas.{GraphicsContext, Canvas}
import javafx.scene.paint.{Stop, CycleMethod, LinearGradient, Color}
import javafx.scene.effect.BlurType
import javafx.scene.text.TextAlignment
import javafx.geometry.VPos
import com.ajjpj.adiagram.render.shapes.{ATextShape, ABoxShape, ALineShape, AShape}
import com.ajjpj.adiagram.render.base._
import com.ajjpj.adiagram.geometry.{ARect, APoint}
import com.ajjpj.adiagram.render.base.LineStyle
import com.ajjpj.adiagram.render.base.FillStyle
import com.ajjpj.adiagram.render.base.ShadowStyle
import scala.Some
import com.ajjpj.adiagram.render.shapes.lineend.{RoundPointedArrowLineEnd, SemiCircleLineEnd, RoundArrowLineEnd, PointedArrowLineEnd}
import com.ajjpj.adiagram.ui.fw.Digest
import com.ajjpj.adiagram.ui.Zoom


/**
 * @author arno
 */
object DrawNiceDemo {
  def main(args: Array[String]) {
    javafx.application.Application.launch(classOf[DrawNiceDemo])
  }
}

class DrawNiceDemo extends javafx.application.Application {
  implicit val digest = new Digest()

  def start(primaryStage: Stage) {
    primaryStage.setTitle("Hello World!")

    val root: StackPane = new StackPane()
    root.getChildren.add(createCanvas())
    primaryStage.setScene(new Scene(root, 1500, 1000))
    primaryStage.show()
  }

  def createCanvas(): Canvas = {
    val width: Double = 1500
    val height: Double = 1000
    val c: Canvas = new Canvas(width, height)
    //    val img1: Image = drawBox(width, height, 50, 50, "Box 1")
    val gc: GraphicsContext = c.getGraphicsContext2D
    //  gc.drawImage(img1, 0, 0)

    gc.setStroke(Color.RED)

    gc.strokeOval(100 - 5, 250 - 5, 10, 10)

    gc.strokeOval(311 - 5, 711 - 5, 10, 10)
    val lineStyle: LineStyle = new LineStyle(Color.BLACK, 10)
    val fillStyle: FillStyle = new FillStyle(new LinearGradient(0.3, 0, .7, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.LIGHTBLUE), new Stop(1, Color.AZURE)))
    val shadowStyle: ShadowStyle = new ShadowStyle(10, 10, 25, BlurType.GAUSSIAN, Color.color(.5, .5, .5))
    val textStyle: TextStyle = new TextStyle(72, TextAlignment.CENTER, VPos.CENTER)
    val lineTextStyle: TextStyle = new TextStyle(24, TextAlignment.CENTER, VPos.CENTER)
    renderShape(gc, new ALineShape(APoint(100, 250), APoint(311, 711), lineStyle, lineTextStyle, new PointedArrowLineEnd, new RoundArrowLineEnd, Some("Dies ist ein Pfeil")))


    renderShape(gc, new ALineShape(APoint(572, 250), APoint(361, 711), lineStyle, lineTextStyle, new RoundPointedArrowLineEnd, new SemiCircleLineEnd, Some("Dies ist ein Pfeil")))
    renderShape(gc, new ABoxShape(ARect(APoint(600, 80), 300, 120), Some("Hi, p"), fillStyle, shadowStyle, textStyle))
    renderShape(gc, new ATextShape(ARect.fromCoordinates(500, 500, 800, 200), "Hallo!", textStyle))

    c
  }

  private def renderShape(gc: GraphicsContext, shape: AShape) {
    val pi: PartialImageWithShadow = shape.render(Zoom.Identity)

    pi.shadow match {
      case Some(sh) => gc.drawImage(sh.img, shape.bounds.topLeft.x + sh.renderOffset.x, shape.bounds.topLeft.y + sh.renderOffset.y)
      case None =>
    }
    gc.drawImage(pi.shape.img, shape.pos.x + pi.shape.renderOffset.x, shape.pos.y + pi.shape.renderOffset.y)
  }
}

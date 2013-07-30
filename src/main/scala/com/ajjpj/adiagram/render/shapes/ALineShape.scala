package com.ajjpj.adiagram.render.shapes

import com.ajjpj.adiagram.render.base.{PartialImageWithShadow, PartialImage, LineStyle, TextStyle}
import com.ajjpj.adiagram.geometry.{Angle, ARect, APoint}
import com.ajjpj.adiagram.render.shapes.lineend.ALineEnd
import javafx.scene.canvas.GraphicsContext
import com.ajjpj.adiagram.geometry.transform.Translation
import javafx.scene.shape.StrokeLineCap
import javafx.scene.transform.{Transform, Rotate, Affine}
import javafx.geometry.VPos
import javafx.scene.text.TextAlignment
import com.ajjpj.adiagram.ui.fw.Digest

/**
 * @author arno
 */
class ALineShape(p0: APoint, p1: APoint, style: LineStyle, textStyle: TextStyle, startDecoration: ALineEnd, endDecoration: ALineEnd, text: Option[String]) extends AShape {
  override val pos = ARect(p0, p1).topLeft

  override val bounds = {
    var decorationWidth = Math.max(startDecoration.width(style), endDecoration.width(style))
    text match {
      case Some(_) => decorationWidth = Math.max(decorationWidth, style.width + textStyle.fontSizeInPixels*2)
      case None =>
    }
    val padding = Math.max(style.width, decorationWidth) / 2

    ARect.createWithPadding(padding, p0, p1)
  }

  val angle = Angle.fromLine(p0, p1)

  override def render(implicit digest: Digest): PartialImageWithShadow = PartialImage.fromGc(pos, bounds, None, (gc, t) => {
    paint(gc, t)
  })

  def paint(gc: GraphicsContext, t: Translation) {
    style.applyTo(gc)

    startDecoration.paint(gc, p0, angle,          style, t)
    endDecoration  .paint(gc, p1, angle.opposite, style, t)

    paintCore(gc, t)

    text match {
      case Some(txt) => paintText(gc, t, txt)
      case None =>
    }
  }

  private def paintCore(gc: GraphicsContext, t: Translation) {
    gc.setLineWidth(style.width)
    gc.setLineCap(StrokeLineCap.BUTT)

    val start = t(p0 + (angle,          startDecoration.shortenLength(style)))
    val end   = t(p1 + (angle.opposite, endDecoration  .shortenLength(style)))

    gc.strokeLine(start.x, start.y, end.x, end.y)
  }

  private def paintText(gc: GraphicsContext, t: Translation, txt: String) {
    val mid = t(p0 halfWayTo p1)
    val ccw = angle.angle <= Math.PI

    val angleUp = if(ccw) angle.ccw90 else angle.cw90
    val textBasePoint = mid + (angleUp, style.width)

    val rotateAngle = if(ccw) -angle.screenDegrees else 180 - angle.screenDegrees
    val rotate: Rotate = Transform.rotate(rotateAngle, textBasePoint.x, textBasePoint.y)
    gc.setTransform(rotate.getMxx(), rotate.getMyx(), rotate.getMxy(), rotate.getMyy(), rotate.getTx(), rotate.getTy());

    textStyle.applyTo(gc)

    gc.setTextBaseline(VPos.BOTTOM)
    gc.setTextAlign(TextAlignment.CENTER)

    gc.fillText(txt, textBasePoint.x, textBasePoint.y)
  }
}



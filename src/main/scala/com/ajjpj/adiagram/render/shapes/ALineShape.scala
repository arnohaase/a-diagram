package com.ajjpj.adiagram.render.shapes

import com.ajjpj.adiagram.render.base.{PartialImage, LineStyle, TextStyle}
import com.ajjpj.adiagram.geometry.{Angle, ARect, APoint}
import com.ajjpj.adiagram.render.shapes.lineend.ALineEnd
import javafx.scene.canvas.GraphicsContext
import com.ajjpj.adiagram.geometry.transform.Translation
import javafx.scene.shape.StrokeLineCap
import javafx.scene.transform.{Transform, Rotate}
import javafx.geometry.VPos
import javafx.scene.text.TextAlignment
import com.ajjpj.adiagram.ui.{AScreenPos, Zoom}

/**
 * @author arno
 */
class ALineShape(p0: APoint, p1: APoint, style: LineStyle, textStyle: TextStyle, startDecoration: ALineEnd, endDecoration: ALineEnd, text: Option[String]) extends AShape {
  override val pos = ARect(p0, p1).topLeft

  override val bounds = {
    var decorationWidth = Math.max(startDecoration.width(style, Zoom.Identity), endDecoration.width(style, Zoom.Identity))
    text match {
      case Some(_) => decorationWidth = Math.max(decorationWidth, style.widthNoZoom + textStyle.height(Zoom.Identity)*2)
      case None =>
    }
    val padding = Math.max(style.widthNoZoom, decorationWidth) / 2

    ARect.createWithPadding(padding, p0, p1)
  }

  val angle = Angle.fromLine(p0, p1)

  override def render(zoom: Zoom) = PartialImage.fromGc(pos, bounds, zoom, None, (gc, t) => {
    paint(gc, t, zoom)
  })

  def paint(gc: GraphicsContext, t: Translation, zoom: Zoom) {
    style.applyTo(gc)

    startDecoration.paint(gc, p0, angle,          style, t, zoom)
    endDecoration  .paint(gc, p1, angle.opposite, style, t, zoom)

    paintCore(gc, t, zoom)

    text match {
      case Some(txt) => paintText(gc, t, txt, zoom)
      case None =>
    }
  }

  private def paintCore(gc: GraphicsContext, t: Translation, zoom: Zoom) {
    gc.setLineWidth(style.width(zoom))
    gc.setLineCap(StrokeLineCap.BUTT)

    val start = AScreenPos.fromModel(t(p0 + (angle,          startDecoration.shortenLengthUnzoomed(style))), zoom)
    val end   = AScreenPos.fromModel(t(p1 + (angle.opposite, endDecoration  .shortenLengthUnzoomed(style))), zoom)

    gc.strokeLine(start.x, start.y, end.x, end.y)
  }

  private def paintText(gc: GraphicsContext, t: Translation, txt: String, zoom: Zoom) {
    val mid: APoint = t(p0 halfWayTo p1)
    val ccw = angle.angle <= Math.PI

    val angleUp = if(ccw) angle.ccw90 else angle.cw90
    val textBasePoint = AScreenPos.fromModel (mid + (angleUp, style.widthNoZoom), zoom)

    val rotateAngle = if(ccw) -angle.screenDegrees else 180 - angle.screenDegrees
    val rotate: Rotate = Transform.rotate(rotateAngle, textBasePoint.x, textBasePoint.y)
    gc.setTransform(rotate.getMxx(), rotate.getMyx(), rotate.getMxy(), rotate.getMyy(), rotate.getTx(), rotate.getTy());

    textStyle.applyTo(gc, zoom)

    gc.setTextBaseline(VPos.BOTTOM)
    gc.setTextAlign(TextAlignment.CENTER)

    gc.fillText(txt, textBasePoint.x, textBasePoint.y)
  }
}



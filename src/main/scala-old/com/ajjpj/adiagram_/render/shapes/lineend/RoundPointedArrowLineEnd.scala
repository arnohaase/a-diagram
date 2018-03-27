package com.ajjpj.adiagram_.render.shapes.lineend

import javafx.scene.canvas.GraphicsContext
import com.ajjpj.adiagram_.geometry.{Angle, APoint}
import com.ajjpj.adiagram_.render.base.LineStyle
import com.ajjpj.adiagram_.geometry.transform.Translation
import javafx.scene.shape.{StrokeLineJoin, StrokeLineCap}
import com.ajjpj.adiagram_.ui.{Zoom, AScreenPos}

/**
 * @author arno
 */
class RoundPointedArrowLineEnd(arrowLineLength: Double = 30.0, arrowAngle: Double = Math.PI/6) extends ALineEnd {
  val sinA = Math.sin(arrowAngle)

  override def shortenLengthUnzoomed(style: LineStyle) = style.widthNoZoom / sinA * .75
  override def width(style: LineStyle, zoom: Zoom) = sinA * arrowLineLength * zoom.factor + style.width(zoom)

  override def paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: Translation, zoom: Zoom) {
    style.applyTo(gc)
    gc.setLineWidth(style.width(zoom))
    gc.setLineCap(StrokeLineCap.ROUND)
    gc.setLineJoin(StrokeLineJoin.MITER)

    val tipRaw: APoint = t.apply(p) + (angle, style.width(Zoom.Identity) / 2 / sinA)
    val tip  = AScreenPos.fromModel(tipRaw, zoom)
    val end0 = AScreenPos.fromModel(tipRaw + (angle + arrowAngle, arrowLineLength), zoom)
    val end1 = AScreenPos.fromModel(tipRaw + (angle - arrowAngle, arrowLineLength), zoom)
    gc.strokePolyline(Array[Double](end0.x, tip.x, end1.x), Array[Double](end0.y, tip.y, end1.y), 3)
  }
}



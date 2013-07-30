package com.ajjpj.adiagram.render.shapes.lineend

import javafx.scene.canvas.GraphicsContext
import com.ajjpj.adiagram.geometry.{Angle, APoint}
import com.ajjpj.adiagram.render.base.LineStyle
import com.ajjpj.adiagram.geometry.transform.Translation
import javafx.scene.shape.{StrokeLineJoin, StrokeLineCap}

/**
 * @author arno
 */
class RoundPointedArrowLineEnd(arrowLineLength: Double = 30.0, arrowAngle: Double = Math.PI/6) extends ALineEnd {
  val sinA = Math.sin(arrowAngle)

  def shortenLength(style: LineStyle) = style.width / sinA * .75
  def width(style: LineStyle) = sinA * arrowLineLength + style.width

  def paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: Translation) {
    style.applyTo(gc)
    gc.setLineWidth(style.width)
    gc.setLineCap(StrokeLineCap.ROUND)
    gc.setLineJoin(StrokeLineJoin.MITER)
    val tip: APoint = t.apply(p) + (angle, style.width / 2 / sinA)
    val end0: APoint = tip + (angle + arrowAngle, arrowLineLength)
    val end1: APoint = tip + (angle - arrowAngle, arrowLineLength)
    gc.strokePolyline(Array[Double](end0.x, tip.x, end1.x), Array[Double](end0.y, tip.y, end1.y), 3)
  }
}



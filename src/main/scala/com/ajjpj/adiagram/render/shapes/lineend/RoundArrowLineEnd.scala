package com.ajjpj.adiagram.render.shapes.lineend

import javafx.scene.canvas.GraphicsContext
import com.ajjpj.adiagram.geometry.{Angle, APoint}
import com.ajjpj.adiagram.render.base.LineStyle
import com.ajjpj.adiagram.geometry.transform.Translation
import javafx.scene.shape.{StrokeLineJoin, StrokeLineCap}

/**
 * @author arno
 */
class RoundArrowLineEnd(arrowLineLength: Double = 30., arrowAngle: Double = Math.PI/6) extends ALineEnd {
  val sinA = Math.sin(arrowAngle)

  override def shortenLength(style: LineStyle) = style.width * .25 //TODO refine this (based on the actual angle)
  override def width(style: LineStyle) = sinA * arrowLineLength + style.width //TODO refine this

  override def paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: Translation) {
    style.applyTo(gc)

    gc.setLineWidth(style.width)
    gc.setLineCap(StrokeLineCap.ROUND)
    gc.setLineJoin(StrokeLineJoin.ROUND)

    val tip = t(p) + (angle, style.width/2)
    val end0 = tip + (angle + arrowAngle, arrowLineLength)
    val end1 = tip + (angle - arrowAngle, arrowLineLength)

    gc.strokePolyline(Array(end0.x, tip.x, end1.x), Array(end0.y, tip.y, end1.y), 3)
  }
}



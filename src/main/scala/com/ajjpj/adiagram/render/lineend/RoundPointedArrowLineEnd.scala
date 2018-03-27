package com.ajjpj.adiagram.render.lineend

import javafx.scene.canvas.GraphicsContext
import javafx.scene.shape.{StrokeLineCap, StrokeLineJoin}

import com.ajjpj.adiagram.geometry.{Angle, LenUnit, Length, Vector2}
import com.ajjpj.adiagram.render.{LineStyle, Model2Screen}


class RoundPointedArrowLineEnd(arrowLengthFactor: Double = 6.0, arrowAngle: Angle = Angle(Math.PI/6)) extends RenderableLineEnd {
  val sinA = Math.sin(arrowAngle.rad)
  def arrowLineLength(style: LineStyle) = {
    // This is a heuristic to give a similar appearance to the arrow head for different line widths
    val a = Math.log(style.width.l) / 2.0 * 1.5
    Length (Math.exp(a) * arrowLengthFactor, style.width.unit)
  }

  override def shortenLength(style: LineStyle, m2s: Model2Screen) = style.width / sinA * .75
  override def width(style: LineStyle, m2s: Model2Screen) = arrowLineLength(style) * 2 * sinA + style.width

  override def paint(gc: GraphicsContext, p: Vector2, angle: Angle, style: LineStyle, t: Vector2, m2s: Model2Screen) {
    style.applyTo(gc, m2s)
    gc.setLineCap(StrokeLineCap.ROUND)
    gc.setLineJoin(StrokeLineJoin.MITER)

    val tipRaw = p + Vector2.fromAngle(angle, style.width / 2 / sinA)
    val tip  = m2s.toScreenCoordinates(tipRaw, t)
    val end0 = m2s.toScreenCoordinates(tipRaw + Vector2.fromAngle(angle + arrowAngle, arrowLineLength(style)), t)
    val end1 = m2s.toScreenCoordinates(tipRaw + Vector2.fromAngle(angle - arrowAngle, arrowLineLength(style)), t)
    gc.strokePolyline(Array(end0.x, tip.x, end1.x), Array(end0.y, tip.y, end1.y), 3)
  }
}



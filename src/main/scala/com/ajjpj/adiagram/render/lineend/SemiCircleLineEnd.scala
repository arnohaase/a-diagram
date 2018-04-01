package com.ajjpj.adiagram.render.lineend

import javafx.scene.canvas.GraphicsContext
import javafx.scene.shape.ArcType

import com.ajjpj.adiagram.geometry.{Angle, Length, Vector2}
import com.ajjpj.adiagram.render.{LineStyle, Model2Screen}


class SemiCircleLineEnd extends RenderableLineEnd {
  override def shortenLength(style: LineStyle, m2s: Model2Screen) = Length.max(Length.ZERO, style.width / 2 - m2s.overlapLength)
  override def width(style: LineStyle, m2s: Model2Screen) = Length.ZERO

  override def paint(gc: GraphicsContext, p: Vector2, angle: Angle, style: LineStyle, t: Vector2, m2s: Model2Screen) {
    val r = style.width / 2
    val rScreen = m2s(r)
    val startAngle = angle.ccw90
    val arcCenter = m2s.toScreenCoordinates (p + Vector2.fromAngle (angle, r), t)
    gc.fillArc(arcCenter.x - rScreen, arcCenter.y - rScreen, m2s(style.width), m2s(style.width), startAngle.screenDegrees, 180, ArcType.ROUND)
  }
}


package com.ajjpj.adiagram.render.lineend

import javafx.scene.canvas.GraphicsContext
import javafx.scene.shape.ArcType

import com.ajjpj.adiagram.geometry.{Angle, Length, Vector2}
import com.ajjpj.adiagram.render.{LineStyle, Model2Screen}


class RoundedCornerLineEnd(cornerFraction: Double = .3) extends RenderableLineEnd {
  def cornerRadius(width: Length) = width/2 * cornerFraction

  override def shortenLength(style: LineStyle, m2s: Model2Screen) = Length.max(Length.ZERO, cornerRadius(style.width) - m2s.overlapPixels)
  override def width(style: LineStyle, m2s: Model2Screen) = Length.ZERO

  override def paint(gc: GraphicsContext, p: Vector2, angle: Angle, style: LineStyle, t: Vector2, m2s: Model2Screen) {
    val r = cornerRadius(style.width)
    val rScreen = m2s(r)

    val c = p + Vector2.fromAngle(angle, r)

    //NB: gc.fillArc's x and y coordinate refer to the upper left corner of the bounding rect of the extrapolated ellipse
    val c0 = m2s.toScreenCoordinates(c + Vector2.fromAngle(angle.ccw90, style.width/2 - r), t)
    gc.fillArc(c0.x - rScreen, c0.y - rScreen, 2*rScreen, 2*rScreen, angle.ccw90.screenDegrees, 90, ArcType.ROUND)

    val c1 = m2s.toScreenCoordinates(c + Vector2.fromAngle(angle.cw90, style.width/2 - r), t)
    gc.fillArc(c1.x - rScreen, c1.y - rScreen, 2*rScreen, 2*rScreen, angle.opposite.screenDegrees, 90, ArcType.ROUND)

    val rect0 = m2s.toScreenCoordinates(p + Vector2.fromAngle(angle.cw90,  style.width/2 - r + m2s.overlapPixels), t)
    val rect1 = m2s.toScreenCoordinates(p + Vector2.fromAngle(angle.ccw90, style.width/2 - r + m2s.overlapPixels), t)
    val rect2 = m2s.toScreenCoordinates(c + Vector2.fromAngle(angle.ccw90, style.width/2 - r + m2s.overlapPixels), t)
    val rect3 = m2s.toScreenCoordinates(c + Vector2.fromAngle(angle.cw90,  style.width/2 - r + m2s.overlapPixels), t)
    gc.fillPolygon(Array(rect0.x, rect1.x, rect2.x, rect3.x), Array(rect0.y, rect1.y, rect2.y, rect3.y), 4)
  }
}



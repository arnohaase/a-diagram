package com.ajjpj.adiagram.render.shapes.lineend

import com.ajjpj.adiagram.render.base.LineStyle
import javafx.scene.canvas.GraphicsContext
import com.ajjpj.adiagram.geometry.{Angle, APoint}
import com.ajjpj.adiagram.geometry.transform.Translation
import javafx.scene.shape.ArcType

/**
 * @author arno
 */
class SemiCircleLineEnd extends ALineEnd {
  override def shortenLength(style: LineStyle) = Math.max(0, style.width / 2 - OVERLAP)
  override def width(style: LineStyle) = 0.

  override def paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: Translation) {
    val r = style.width / 2
    val center = p + (angle, r)
    val startAngle = angle.ccw90.screenDegrees
    val arcPoint = t(center.x - r, center.y - r)
    gc.fillArc(arcPoint.x, arcPoint.y, style.width, style.width, startAngle, 180, ArcType.ROUND)
  }
}


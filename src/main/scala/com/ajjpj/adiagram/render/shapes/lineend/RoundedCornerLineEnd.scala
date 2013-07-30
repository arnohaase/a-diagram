package com.ajjpj.adiagram.render.shapes.lineend

import javafx.scene.canvas.GraphicsContext
import com.ajjpj.adiagram.geometry.{Angle, APoint}
import com.ajjpj.adiagram.render.base.LineStyle
import com.ajjpj.adiagram.geometry.transform.Translation
import javafx.scene.shape.ArcType

/**
 * @author arno
 */
class RoundedCornerLineEnd(cornerFraction: Double) extends ALineEnd {
  def cornerRadius(width: Double) = width/2 * cornerFraction

  override def shortenLength(style: LineStyle): Double = Math.max(0, cornerRadius(style.width) - OVERLAP);
  override def width(style: LineStyle) = 0.

  override def paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: Translation) {
    val r = style.width / 2
    val cr = cornerRadius(style.width)

    val center = p + (angle, cr)

    val center0 = center + (angle.ccw90, r - cr)
    val p0 = t(center0.x - cr, center0.y - cr)
    gc.fillArc(p0.x, p0.y, 2*cr, 2*cr, angle.ccw90.screenDegrees, 90, ArcType.ROUND)

    val center1 = center + (angle.cw90, r - cr)
    val p1 = t(center1.x - cr, center1.y - cr)
    gc.fillArc(p1.x, p1.y, 2*cr, 2*cr, angle.opposite.screenDegrees, 90, ArcType.ROUND)

    val rect0 = t(p + (angle.cw90,  r - cr + OVERLAP))
    val rect1 = t(p + (angle.ccw90, r - cr + OVERLAP))
    val rect2 = t(center + (angle.ccw90, r - cr + OVERLAP))
    val rect3 = t(center + (angle.cw90,  r - cr + OVERLAP))
    gc.fillPolygon(Array(rect0.x, rect1.x, rect2.x, rect3.x), Array(rect0.y, rect1.y, rect2.y, rect3.y), 4)
  }
}



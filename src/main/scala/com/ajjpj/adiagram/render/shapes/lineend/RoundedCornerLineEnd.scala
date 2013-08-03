package com.ajjpj.adiagram.render.shapes.lineend

import javafx.scene.canvas.GraphicsContext
import com.ajjpj.adiagram.geometry.{Angle, APoint}
import com.ajjpj.adiagram.render.base.LineStyle
import com.ajjpj.adiagram.geometry.transform.Translation
import javafx.scene.shape.ArcType
import com.ajjpj.adiagram.ui.{AScreenPos, Zoom}

/**
 * @author arno
 */
class RoundedCornerLineEnd(cornerFraction: Double) extends ALineEnd {
  def cornerRadius(width: Double) = width/2 * cornerFraction

  override def shortenLengthUnzoomed(style: LineStyle): Double = Math.max(0, cornerRadius(style.widthNoZoom) - OVERLAP)
  override def width(style: LineStyle, zoom: Zoom) = 0.0

  override def paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: Translation, zoom: Zoom) {
    val rUnzoomed = style.width(Zoom.Identity) / 2
    val crUnzoomed = cornerRadius(style.width(Zoom.Identity))
    val cr = cornerRadius(style.width(zoom))

    val center = p + (angle, crUnzoomed)

    val center0 = center + (angle.ccw90, rUnzoomed - crUnzoomed)
    val p0 = AScreenPos.fromModel(t(center0.x - crUnzoomed, center0.y - crUnzoomed), zoom)
    gc.fillArc(p0.x, p0.y, 2*cr, 2*cr, angle.ccw90.screenDegrees, 90, ArcType.ROUND)

    val center1 = center + (angle.cw90, rUnzoomed - crUnzoomed)
    val p1 = AScreenPos.fromModel(t(center1.x - crUnzoomed, center1.y - crUnzoomed), zoom)
    gc.fillArc(p1.x, p1.y, 2*cr, 2*cr, angle.opposite.screenDegrees, 90, ArcType.ROUND)

    val rect0 = AScreenPos.fromModel(t(p + (angle.cw90,  rUnzoomed - crUnzoomed + OVERLAP/zoom.factor)), zoom)
    val rect1 = AScreenPos.fromModel(t(p + (angle.ccw90, rUnzoomed - crUnzoomed + OVERLAP/zoom.factor)), zoom)
    val rect2 = AScreenPos.fromModel(t(center + (angle.ccw90, rUnzoomed - crUnzoomed + OVERLAP/zoom.factor)), zoom)
    val rect3 = AScreenPos.fromModel(t(center + (angle.cw90,  rUnzoomed - crUnzoomed + OVERLAP/zoom.factor)), zoom)
    gc.fillPolygon(Array(rect0.x, rect1.x, rect2.x, rect3.x), Array(rect0.y, rect1.y, rect2.y, rect3.y), 4)
  }
}



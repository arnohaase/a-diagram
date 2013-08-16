package com.ajjpj.adiagram.render.shapes.lineend

import com.ajjpj.adiagram.render.base.LineStyle
import javafx.scene.canvas.GraphicsContext
import com.ajjpj.adiagram.geometry.{Angle, APoint}
import com.ajjpj.adiagram.geometry.transform.Translation
import javafx.scene.shape.ArcType
import com.ajjpj.adiagram.ui.{Zoom, AScreenPos}

/**
 * @author arno
 */
class SemiCircleLineEnd extends ALineEnd {
  override def shortenLengthUnzoomed(style: LineStyle) = Math.max(0, style.widthNoZoom / 2 - OVERLAP)
  override def width(style: LineStyle, zoom: Zoom) = 0.0

  override def paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: Translation, zoom: Zoom) {
    val rUnzoomed = style.width(Zoom.Identity) / 2
    val center = p + (angle, rUnzoomed)
    val startAngle = angle.ccw90.screenDegrees

    val arcPoint = AScreenPos.fromModel(t(center.x - rUnzoomed, center.y - rUnzoomed), zoom)
    gc.fillArc(arcPoint.x, arcPoint.y, style.width(zoom), style.width(zoom), startAngle, 180, ArcType.ROUND)
  }
}


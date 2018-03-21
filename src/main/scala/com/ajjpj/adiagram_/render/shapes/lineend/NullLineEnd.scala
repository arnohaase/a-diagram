package com.ajjpj.adiagram_.render.shapes.lineend

import javafx.scene.canvas.GraphicsContext
import com.ajjpj.adiagram_.geometry.{Angle, APoint}
import com.ajjpj.adiagram_.render.base.LineStyle
import com.ajjpj.adiagram_.geometry.transform.Translation
import com.ajjpj.adiagram_.ui.Zoom

/**
 * @author arno
 */
class NullLineEnd extends ALineEnd {
  override def shortenLengthUnzoomed(style: LineStyle) = 0.0
  override def width(style: LineStyle, zoom: Zoom) = 0.0

  override def paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: Translation, zoom: Zoom) {
  }
}

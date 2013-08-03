package com.ajjpj.adiagram.render.shapes.lineend

import javafx.scene.canvas.GraphicsContext
import com.ajjpj.adiagram.geometry.{Angle, APoint}
import com.ajjpj.adiagram.render.base.LineStyle
import com.ajjpj.adiagram.geometry.transform.Translation
import com.ajjpj.adiagram.ui.Zoom

/**
 * @author arno
 */
class NullLineEnd extends ALineEnd {
  override def shortenLengthUnzoomed(style: LineStyle) = 0.0
  override def width(style: LineStyle, zoom: Zoom) = 0.0

  override def paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: Translation, zoom: Zoom) {
  }
}

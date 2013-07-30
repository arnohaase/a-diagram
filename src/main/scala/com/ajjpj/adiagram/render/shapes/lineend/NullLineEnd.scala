package com.ajjpj.adiagram.render.shapes.lineend

import javafx.scene.canvas.GraphicsContext
import com.ajjpj.adiagram.geometry.{Angle, APoint}
import com.ajjpj.adiagram.render.base.LineStyle
import com.ajjpj.adiagram.geometry.transform.Translation

/**
 * @author arno
 */
class NullLineEnd extends ALineEnd {
  def shortenLength(style: LineStyle) = 0.

  def width(style: LineStyle) = 0.

  def paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: Translation) {

  }
}

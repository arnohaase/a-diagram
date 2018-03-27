package com.ajjpj.adiagram_.render.shapes.lineend

import com.ajjpj.adiagram_.render.base.LineStyle
import javafx.scene.canvas.GraphicsContext
import com.ajjpj.adiagram_.geometry.{Angle, APoint}
import com.ajjpj.adiagram_.geometry.transform.Translation
import com.ajjpj.adiagram_.ui.Zoom

/**
 * @author arno
 */
trait ALineEnd {
  /**
   * The shortening of the actual line is reduced by this distance to avoid unpainted artifacts where
   * line and line end decoration meet. This may require some experimentation and cross-platform testing!
   */
  val OVERLAP = 0.5

  def shortenLengthUnzoomed(style: LineStyle): Double

  def width(style: LineStyle, zoom: Zoom): Double

  /**
   * @param p is the end of the physical line *including* the line ending (i.e. before
   *          being corrected with the result of getShortenLength())
   * @param angle direction of the line as if it was *starting* from this end
   */
  def paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: Translation, zoom: Zoom): Unit
}

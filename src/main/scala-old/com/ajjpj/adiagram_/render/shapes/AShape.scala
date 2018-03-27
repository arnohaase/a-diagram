package com.ajjpj.adiagram_.render.shapes

import com.ajjpj.adiagram_.geometry.{APoint, ARect}
import com.ajjpj.adiagram_.render.base.PartialImageWithShadow
import com.ajjpj.adiagram_.ui.Zoom


/**
 * @author arno
 */
trait AShape {
  def pos: APoint = bounds.topLeft //TODO remove this - assume the 'pos' to always be (0, 0) (?)
  def bounds: ARect
  def renderBounds: ARect // including space for arrow heads, shadows etc.
  def render(zoom: Zoom): PartialImageWithShadow
}

package com.ajjpj.adiagram.render.shapes

import com.ajjpj.adiagram.geometry.{APoint, ARect}
import com.ajjpj.adiagram.render.base.PartialImageWithShadow
import com.ajjpj.adiagram.ui.Zoom


/**
 * @author arno
 */
trait AShape {
  def pos: APoint = bounds.topLeft //TODO remove this - assume the 'pos' to always be (0, 0) (?)
  def bounds: ARect
  def render(zoom: Zoom): PartialImageWithShadow
}

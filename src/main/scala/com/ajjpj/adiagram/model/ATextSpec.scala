package com.ajjpj.adiagram.model

import com.ajjpj.adiagram.geometry.{ARect, ADim, APoint}
import com.ajjpj.adiagram.render.shapes.ATextShape
import com.ajjpj.adiagram.render.base.TextStyle

/**
 * @author arno
 */
class ATextSpec(var pos: APoint, var dim: ADim, var text: String, var textStyle: TextStyle) extends AShapeSpec {
  protected def shape = new ATextShape(ARect(pos, dim), text, textStyle)

  def resizeBy(delta: ADim) = dim += delta

  protected def doMoveBy(delta: APoint) = pos += delta
}

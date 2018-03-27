package com.ajjpj.adiagram_.model.diagram

import com.ajjpj.adiagram_.geometry.{ARect, ADim, APoint}
import com.ajjpj.adiagram_.render.shapes.ATextShape
import com.ajjpj.adiagram_.render.base.TextStyle
import com.ajjpj.adiagram_.model.style.TextStyleSpec

/**
 * @author arno
 */
class ATextSpec(var pos: APoint, var dim: ADim, var text: String, var textStyle: TextStyleSpec) extends AShapeSpec {
  override def shape = new ATextShape(ARect(pos, dim), text, textStyle.style)

  override def resizeBy(delta: ADim) = dim += delta
  override protected def doMoveBy(delta: APoint) = pos += delta

  override def snapshot = TextSpecSnapshot (dim, text, textStyle.style)

  case class TextSpecSnapshot(dim: ADim, text: String, textStyle: TextStyle) extends ShapeSpecReRenderSnapshot
}

package com.ajjpj.adiagram.model

import com.ajjpj.adiagram.geometry.{ARect, ADim, APoint}
import com.ajjpj.adiagram.render.shapes.ATextShape
import com.ajjpj.adiagram.render.base.TextStyle

/**
 * @author arno
 */
class ATextSpec(var pos: APoint, var dim: ADim, var text: String, var textStyle: TextStyle) extends AShapeSpec {
  override def shape = new ATextShape(ARect(pos, dim), text, textStyle)

  override def resizeBy(delta: ADim) = dim += delta
  override protected def doMoveBy(delta: APoint) = pos += delta

  override def snapshot = TextSpecSnapshot (dim, text, textStyle)

  case class TextSpecSnapshot(dim: ADim, text: String, textStyle: TextStyle) extends ShapeSpecReRenderSnapshot
}

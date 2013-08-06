package com.ajjpj.adiagram.model.diagram

import com.ajjpj.adiagram.geometry.{APoint, ARect, ADim}
import com.ajjpj.adiagram.render.shapes.{ABoxShape, AShape}
import com.ajjpj.adiagram.render.base.{ShadowStyle, TextStyle, FillStyle}


/**
 * @author arno
 */
class ABoxSpec(var pos: APoint, var dim: ADim, var text: Option[String], var fillStyle: FillStyle, var shadowStyle: ShadowStyle, var textStyle: TextStyle) extends AShapeSpec {
  override def resizeBy(delta: ADim) = dim += delta

  override def shape: AShape = new ABoxShape(ARect(pos, dim), text, fillStyle, shadowStyle, textStyle)
  override protected def doMoveBy(delta: APoint) = pos += delta

  override def snapshot = BoxSpecSnapshot(dim, text, fillStyle, shadowStyle, textStyle)

  case class BoxSpecSnapshot(dim: ADim, text: Option[String], fillStyle: FillStyle, shadowStyle: ShadowStyle, textStyle: TextStyle) extends ShapeSpecReRenderSnapshot
}

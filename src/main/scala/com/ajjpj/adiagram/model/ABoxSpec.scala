package com.ajjpj.adiagram.model

import com.ajjpj.adiagram.geometry.{APoint, ARect, ADim}
import com.ajjpj.adiagram.render.shapes.{ABoxShape, AShape}
import com.ajjpj.adiagram.render.base.{ShadowStyle, TextStyle, FillStyle}


/**
 * @author arno
 */
class ABoxSpec(var dim: ADim, var text: Option[String], var fillStyle: FillStyle, var shadowStyle: ShadowStyle, var textStyle: TextStyle) extends AShapeSpec {
  var pos = APoint(0, 0)
  def resizeBy(delta: ADim) = dim += delta

  protected def shape: AShape = new ABoxShape(ARect(pos, dim), text, fillStyle, shadowStyle, textStyle)
  protected def doMoveBy(delta: APoint) = pos += delta
}

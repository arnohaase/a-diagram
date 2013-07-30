package com.ajjpj.adiagram.model

import com.ajjpj.adiagram.geometry.{ARect, ADim, APoint}
import com.ajjpj.adiagram.render.shapes.ALineShape
import com.ajjpj.adiagram.render.base.{TextStyle, LineStyle}
import com.ajjpj.adiagram.render.shapes.lineend.{RoundPointedArrowLineEnd, RoundedCornerLineEnd}

/**
 * @author arno
 */
class ALineSpec(var p0: APoint, var p1: APoint, var text: Option[String], lineStyle: LineStyle, textStyle: TextStyle) extends AShapeSpec {
  protected override def shape = new ALineShape(p0, p1, lineStyle, textStyle, new RoundedCornerLineEnd(.5), new RoundPointedArrowLineEnd(), text)

  override def boundsForResizing = ARect(p0, p1)
  protected def pos = boundsForResizing.topLeft

  def resizeBy(delta: ADim) {
    if(p0.x > p1.x) {
      p0 = p0.copy(x = p0.x + delta.width)
    }
    else {
      p1 = p1.copy(x = p1.x + delta.width)
    }

    if(p0.y > p1.y) {
      p0 = p0.copy(y = p0.y + delta.height)
    }
    else {
      p1 = p1.copy(y = p1.y + delta.height)
    }
  }

  protected def doMoveBy(delta: APoint) {
    p0 += delta
    p1 += delta
  }
}

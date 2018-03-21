package com.ajjpj.adiagram_.model.diagram

import com.ajjpj.adiagram_.geometry.{ARect, ADim, APoint}
import com.ajjpj.adiagram_.render.shapes.ALineShape
import com.ajjpj.adiagram_.render.base.LineStyle
import com.ajjpj.adiagram_.render.base.TextStyle
import com.ajjpj.adiagram_.model.style.{LineEndSpec, LineStyleSpec, TextStyleSpec}
import com.ajjpj.adiagram_.render.shapes.lineend.ALineEnd

/**
 * @author arno
 */
class ALineSpec(var text: Option[String], var lineStyle: LineStyleSpec, var textStyle: TextStyleSpec, var startLineEnd: LineEndSpec, var endLineEnd: LineEndSpec) extends AShapeSpec {
  def this(initialP0: APoint, initialP1: APoint, text: Option[String], lineStyle: LineStyleSpec, textStyle: TextStyleSpec, startLineEnd: LineEndSpec, endLineEnd: LineEndSpec) = {
    this(text, lineStyle, textStyle, startLineEnd, endLineEnd)
    p0Source = new LiteralPosSource(initialP0)
    p1Source = new LiteralPosSource(initialP1)
  }
  override def shape = new ALineShape(p0Source.pos, p1Source.pos, lineStyle.style, textStyle.style, startLineEnd.lineEnd, endLineEnd.lineEnd, text)
//  override def shape = new ALineShape(p0Source.pos, p1Source.pos, lineStyle, textStyle, new RoundedCornerLineEnd(.5), new RoundPointedArrowLineEnd(), text) //TODO configurable line ends

  var p0Source: PosSource = new LiteralPosSource(APoint.ZERO)
  var p1Source: PosSource = new LiteralPosSource(APoint.ZERO)

  def bindStartPoint(box: ABoxSpec) = p0Source = new BoxPosSource(box, p1Source)
  def unbindStartPoint() = p0Source = new LiteralPosSource(p0Source.pos)

  def bindEndPoint(box: ABoxSpec) = p1Source = new BoxPosSource(box, p0Source)
  def unbindEndPoint() = p1Source = new LiteralPosSource(p1Source.pos)

  override def boundsForResizing = ARect(p0Source.pos, p1Source.pos)
  override def pos = boundsForResizing.topLeft
  override def pos_= (p: APoint) = throw new UnsupportedOperationException()

  def resizeBy(delta: ADim) = throw new UnsupportedOperationException

  protected def doMoveBy(delta: APoint) {
    p0Source = new LiteralPosSource(p0Source.pos + delta)
    p1Source = new LiteralPosSource(p1Source.pos + delta)
  }

  override def snapshot = ALineSpecSnapshot(p0Source.pos, p1Source.pos, text, lineStyle.style, textStyle.style, startLineEnd.lineEnd, endLineEnd.lineEnd)
}

trait PosSource {
  def pos: APoint
  def unclippedPos = pos

  def isDerived: Boolean

  def + (delta: APoint) = LiteralPosSource (pos + delta)
  def - (delta: APoint) = LiteralPosSource (pos - delta)
}
case class LiteralPosSource(pos: APoint) extends PosSource {
  override def isDerived = false
}
case class BoxPosSource(box: ABoxSpec) extends PosSource {
  def this(box: ABoxSpec, opposite: => PosSource) = {this(box); this.opposite = () => opposite}
  var opposite: () => PosSource = _
  def rect = ARect(box.pos, box.dim)
  override def unclippedPos = rect.center
  override def pos = rect.intersection(unclippedPos, opposite().unclippedPos)
  override def isDerived = true
}

case class ALineSpecSnapshot(p0: APoint, p1: APoint, text: Option[String], lineStyle: LineStyle, textStyle: TextStyle, startEnd: ALineEnd, endEnd: ALineEnd) extends ShapeSpecReRenderSnapshot



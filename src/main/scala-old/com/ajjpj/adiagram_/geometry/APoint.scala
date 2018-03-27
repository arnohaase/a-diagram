package com.ajjpj.adiagram_.geometry

import scala.language.implicitConversions


/**
 * @author arno
 */
case class APoint(x: Double, y: Double) {
  def halfWayTo(p: APoint) = APoint((x+p.x)/2, (y+p.y)/2)
  def inverse = APoint(-x, -y)

  def +(p: (Double, Double)): APoint = APoint(x+p._1, y+p._2)
  def +(p: APoint): APoint = APoint(x+p.x, y+p.y)
  def +(angle: Angle, d: Double):APoint = this + ((d*angle.unitX, d*angle.unitY))

  def -(p: APoint): APoint = APoint(x-p.x, y-p.y)

  def distanceTo(p: APoint) = Math.sqrt((p.x-x)*(p.x-x) + (p.y-y)*(p.y-y))
}

object APoint {
  val ZERO = APoint(0, 0)

  implicit def pointToPair  (p: APoint) = (p.x, p.y)
  implicit def pointFromPair(p: (Double, Double)) = APoint(p._1, p._2)
}

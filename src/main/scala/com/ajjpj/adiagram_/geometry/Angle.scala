package com.ajjpj.adiagram_.geometry


/**
 * This class represents an angle. If used in an absolute context, it counts counterclockwise from 'down'.
 *
 * @author arno
 */
class Angle(val angle: Double) {
  /** converts the angle to the JavaFX representation, i.e. degrees ccw from 'horizontal to the right' */
  def screenDegrees = (angle * 180 / Math.PI + 270 + 360) % 360

  def unitX = Math.sin(angle)
  def unitY = Math.cos(angle)

  def opposite = Angle(angle + Math.PI)

  def cw90 = Angle(angle - Math.PI/2)
  def ccw90 = Angle(angle + Math.PI/2)

  def +(other: Angle): Angle = Angle(angle + other.angle)
  def +(a: Double) = Angle(angle + a)
  def -(other: Angle): Angle = Angle(angle - other.angle)
  def -(a: Double) = Angle(angle - a)

  override def toString = "Angle{" + angle + "}"
}

object Angle {
  def apply(angle: Double) = {
    var a = angle % (2*Math.PI)
    if(a < 0) {
      a += + 2*Math.PI
    }
    new Angle(a)
  }
  def fromDxDy(dx: Double, dy: Double) = Angle(Math.atan2(dx, dy))
  def fromLine(p0: APoint, p1: APoint) = fromDxDy(p1.x - p0.x, p1.y - p0.y)
}

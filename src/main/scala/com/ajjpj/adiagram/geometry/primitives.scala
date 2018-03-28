package com.ajjpj.adiagram.geometry


class LenUnit (val factorToPoint: Double) extends AnyVal {
  def factorFromPoint = 1 / factorToPoint
  def convertTo (x: Double, newUnit: LenUnit) = x * factorToPoint * newUnit.factorFromPoint

  override def toString = s"LenUnit{$factorFromPoint}"
}
object LenUnit {
  def apply(factorToPoint: Double) = new LenUnit(factorToPoint)

  val pt = LenUnit(1)
  val inch = LenUnit(72)
  val mm = LenUnit(inch.factorToPoint / 25.4)
}

/**
  * This class represents an angle. If used in an absolute context, it counts counterclockwise from 'down' (which has positive y).
  */
class Angle(val rad: Double) extends AnyVal {
  /** converts the angle to the JavaFX representation, i.e. degrees ccw from 'horizontal to the right' */
  def screenDegrees = (rad * 180 / Math.PI + 270 + 360) % 360

  def unitX = Math.sin(rad)
  def unitY = Math.cos(rad)

  def opposite = Angle(rad + Math.PI)

  def cw90 = Angle(rad - Math.PI/2)
  def ccw90 = Angle(rad + Math.PI/2)

  def +(other: Angle): Angle = Angle(rad + other.rad)
  def -(other: Angle): Angle = Angle(rad - other.rad)

  override def toString = s"Angle{$rad}"
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
  def fromLine(p0: Vector2, p1: Vector2) = {
    val p1Norm = p1.inUnit(p0.unit)
    fromDxDy(p1Norm.x - p0.x, p1Norm.y - p0.y)
  }
}

case class Length(l: Double, unit: LenUnit) {
  def inUnit(u: LenUnit) = Length(unit.convertTo(l, u), u) //TODO test

  def +(other: Length) = Length(l + other.inUnit(unit).l, unit) // TODO test
  def -(other: Length) = Length(l - other.inUnit(unit).l, unit) // TODO test

  def *(f: Double) = Length(l*f, unit) //TODO test
  def /(f: Double) = Length(l/f, unit) //TODO test
}
object Length {
  val ZERO = Length(0, LenUnit.pt)
  def max(l1: Length, _l2: Length) = { //TODO test
    val l2 = _l2.inUnit(l1.unit)
    if (l1.l >= l2.l) l1 else l2
  }
}

/**
  * Coordinates are in screen orientation, i.e. x goes from left to right and y from top to bottom
  */
case class Vector2 (x: Double, y: Double, unit: LenUnit) {
  def inUnit(u: LenUnit) = if(u == unit) this else Vector2(unit.convertTo(x, u), unit.convertTo(y, u), u)

  val xLen = Length(x, unit)
  val yLen = Length(y, unit)

  def halfWayTo(p: Vector2) = Vector2((x+p.inUnit(unit).x)/2, (y+p.inUnit(unit).y)/2, unit)
  def inverse = Vector2(-x, -y, unit)

  def +(p: Vector2): Vector2 = Vector2(x+p.inUnit(unit).x, y+p.inUnit(unit).y, unit)
  def -(p: Vector2): Vector2 = this + p.inverse

  def *(f: Double) = Vector2(x*f, y*f, unit) //TODO test
  def /(f: Double) = Vector2(x/f, y/f, unit) //TODO test

  def distanceTo(p: Vector2): Length = {
    val pNorm = p.inUnit(unit)
    Length (Math.sqrt((pNorm.x-x)*(pNorm.x-x) + (pNorm.y-y)*(pNorm.y-y)), unit)
  }
}
object Vector2 {
//  def fromLength(x: Length, y: Length): Vector2 = Vector2(x.l, y.inUnit(x.unit).l, x.unit)
  def fromAngle(angle: Angle, len: Length): Vector2 = Vector2 (len.l*angle.unitX, len.l*angle.unitY, len.unit)
  final val ZERO = Vector2(0, 0, LenUnit.pt)
}
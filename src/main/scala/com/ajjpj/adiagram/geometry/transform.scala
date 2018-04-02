package com.ajjpj.adiagram.geometry


class Matrix2 (val m00: Double, val m10: Double, val m01: Double, val m11: Double) {
  final val det = m00 * m11 - m10 * m01
  lazy val inverse = Matrix2(m11/det, -m10/det, -m01/det, m00/det)
  def *(p: Vector2): Vector2 = Vector2 (m00*p.x + m01*p.y, m10*p.x + m11*p.y, p.unit)
  def *(o: Matrix2): Matrix2 = Matrix2 (m00 * o.m00 + m01 * o.m10, m10 * o.m00 + m11 * o.m10, m00 * o.m01 + m01 * o.m11, m10 * o.m01 + m11 * o.m11)

  override def toString = s"Matrix{m00=$m00, m01=$m01, m10=$m10, m11=$m11}"
}
object Matrix2 {
  import Math._

  def apply(m00: Double, m10: Double, m01: Double, m11: Double) = new Matrix2 (m00, m10, m01, m11)

  def scale(factor: Double) = Matrix2(factor, 0, 0, factor)
  def rotate(angle: Double) = Matrix2(cos(angle), -sin(angle), sin(angle), cos(angle))

  val UNIT = Matrix2(1, 0, 0, 1)
}

/**
  * the semantics of the constructor data structures are as follows:
  *
  * y = M * x + t
  *
  * i.e. a point is first multiplied by the transformation matrix and then translated by the translation vector
  */
case class AffineTransformation(translation: Vector2, m: Matrix2) {
  def apply(p: Vector2): Vector2 = m*p + translation
//  def apply(rect: ARect): ARect = ARect(this(rect.topLeft), this(rect.bottomRight))

  def inverse = AffineTransformation(m.inverse, (m.inverse*translation).inverse)

  def before(other: AffineTransformation) = other.after(this)
  def after(other: AffineTransformation) = AffineTransformation(m * other.m, m*other.translation + translation)

  def scaleFactor = m.det
}

object AffineTransformation {
  def apply(m: Matrix2, translation: Vector2) = new AffineTransformation(translation, m)
  def translation(translation: Vector2) = new AffineTransformation(translation, Matrix2.UNIT)

  def scaling(origin: Vector2, factor: Double) =
    translation(origin) after
      AffineTransformation(Matrix2.scale(factor), Vector2.ZERO) after
      translation (origin.inverse)

  def rotation(origin: Vector2, angle: Angle) =
    translation(origin) after
      AffineTransformation(Matrix2.rotate(angle.rad), Vector2.ZERO) after
      translation (origin.inverse)
}

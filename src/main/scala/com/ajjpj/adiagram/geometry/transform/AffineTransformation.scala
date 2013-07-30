package com.ajjpj.adiagram.geometry.transform

import com.ajjpj.adiagram.geometry.{Angle, ARect, APoint}


/**
 * the semantics of the constructor data structures are as follows:
 *
 * y = M * x + t
 *
 * i.e. a point is first multiplied by the transformation matrix and then translated by the translation vector
 *
 * @author arno
 */
class AffineTransformation(val translation: APoint, val m: Matrix) {
  def apply(p: APoint): APoint = m*p + translation
  def apply(rect: ARect): ARect = ARect(this(rect.topLeft), this(rect.bottomRight))

  def inverse = AffineTransformation(m.inverse, (m.inverse*translation).inverse)

  def before(other: AffineTransformation) = other.after(this)
  def after(other: AffineTransformation) = AffineTransformation(m * other.m, m*other.translation + translation)

  def scaleFactor = m.det

  override def toString = "AffineTransformation{" +
      "translation=" + translation +
      ", m=" + m +
      '}';
}

object AffineTransformation {
  def apply(m: Matrix, translation: APoint) = new AffineTransformation(translation, m)
  def translation(translation: APoint) = new AffineTransformation(translation, Matrix.UNIT)

  def scaling(origin: APoint, factor: Double) = translation(origin) after
    AffineTransformation(Matrix.scale(factor), APoint.ZERO) after
    translation (origin.inverse)

  def rotation(origin: APoint, angle: Angle) = translation(origin) after
    AffineTransformation(Matrix.rotate(angle.angle), APoint.ZERO) after
    translation (origin.inverse)
}

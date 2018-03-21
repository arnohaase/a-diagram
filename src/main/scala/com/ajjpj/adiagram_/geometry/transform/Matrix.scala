package com.ajjpj.adiagram_.geometry.transform

import com.ajjpj.adiagram_.geometry.APoint


/**
 * @author arno
 */
private[transform] class Matrix(val m00: Double, val m10: Double, val m01: Double, val m11: Double) {
  lazy val det = m00 * m11 - m10 * m01

  lazy val inverse = Matrix(m11/det, -m10/det, -m01/det, m00/det)
  def *(p: APoint): APoint = APoint (m00*p.x + m01*p.y, m10*p.x + m11*p.y)
  def *(o: Matrix): Matrix = Matrix (m00 * o.m00 + m01 * o.m10, m10 * o.m00 + m11 * o.m10, m00 * o.m01 + m01 * o.m11, m10 * o.m01 + m11 * o.m11)

  override def toString = "Matrix{" + "m00=" + m00 + ", m01=" + m01 + ", m10=" + m10 + ", m11=" + m11 + '}'
}

private[transform] object Matrix {
  import Math._

  def apply(m00: Double, m10: Double, m01: Double, m11: Double) = new Matrix (m00, m10, m01, m11)

  def scale(factor: Double) = Matrix(factor, 0, 0, factor)
  def rotate(angle: Double) = Matrix(cos(angle), -sin(angle), sin(angle), cos(angle))

  val UNIT = Matrix(1, 0, 0, 1)
}



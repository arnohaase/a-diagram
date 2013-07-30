package com.ajjpj.adiagram.geometry


/**
 * @author arno
 */
trait GeometricShape {
  def contains(p: APoint): Boolean
  def intersection(inside: APoint, outside: APoint): APoint
}

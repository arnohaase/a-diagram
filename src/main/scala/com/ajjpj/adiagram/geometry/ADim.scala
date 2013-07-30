package com.ajjpj.adiagram.geometry

import scala.language.implicitConversions


/**
 * @author arno
 */
case class ADim(width: Double, height: Double) {
  def +(other: ADim) = ADim(width + other.width, height + other.height)
}

object ADim {
  implicit def dimFromDoublePair(pair: (Double, Double)) = ADim(pair._1, pair._2)
}
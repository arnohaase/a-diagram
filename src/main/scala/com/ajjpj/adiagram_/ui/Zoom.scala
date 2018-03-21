package com.ajjpj.adiagram_.ui

/**
 * @author arno
 */
case class Zoom(factor: Double) {
  def *(scale: Double) = Zoom(factor * scale) //TODO rounding?
}

object Zoom {
  val Identity = Zoom(1)
}

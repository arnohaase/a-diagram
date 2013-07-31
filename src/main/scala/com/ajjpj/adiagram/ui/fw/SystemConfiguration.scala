package com.ajjpj.adiagram.ui.fw

/**
 * @author arno
 */
object SystemConfiguration {
  val maxBindingRefreshIterations = 10

  val selectToDragThreshold = 10.0

  val distanceOfHandlesFromShapes   = 10.0
  val distanceOfHandlesFromShapesXY = distanceOfHandlesFromShapes / Math.sqrt(2)
}

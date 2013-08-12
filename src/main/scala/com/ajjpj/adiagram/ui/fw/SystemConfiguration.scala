package com.ajjpj.adiagram.ui.fw

/**
 * @author arno
 */
object SystemConfiguration {
  val leftAccordionWidth = 250

  val maxBindingRefreshIterations = 10

  val selectToDragThreshold = 10.0

  val distanceOfHandlesFromShapes   = 10.0
  val distanceOfHandlesFromShapesXY = distanceOfHandlesFromShapes / Math.sqrt(2)

  val exportDefaultZoom = 2.0
  val exportRenderTransparentBackground = false
  val exportRenderBorder = false

  val exportMinZoom = .1
  val exportMaxZoom = 20.0
}

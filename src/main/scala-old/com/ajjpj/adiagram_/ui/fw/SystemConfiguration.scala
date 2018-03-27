package com.ajjpj.adiagram_.ui.fw

/**
 * @author arno
 */
object SystemConfiguration {
  val leftAccordionWidth = 350

  val maxBindingRefreshIterations = 10

  val boxDragTargetRadius = 15.0
  val dragHandleSize = 10.0
  val selectToDragThreshold = 10.0

  val distanceOfHandlesFromShapes   = 10.0
  val distanceOfHandlesFromShapesXY = distanceOfHandlesFromShapes / Math.sqrt(2)

  val exportDefaultZoom = 2.0
  val exportRenderTransparentBackground = false
  val exportRenderBorder = false

  val exportMinZoom = .1
  val exportMaxZoom = 20.0
}

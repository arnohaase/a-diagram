package com.ajjpj.adiagram.ui

import com.ajjpj.adiagram.geometry.ARect

/**
 * @author arno
 */
case class AScreenRect (topLeft: AScreenPos, bottomRight: AScreenPos) {
  def topRight   = AScreenPos(bottomRight.x, topLeft.y)
  def bottomLeft = AScreenPos(topLeft.x,     bottomRight.y)

  def width = bottomRight.x - topLeft.x
  def height = bottomRight.y - topLeft.y

  def withPadding (padding: Double) = AScreenRect(AScreenPos(topLeft.x - padding, topLeft.y - padding), AScreenPos(bottomRight.x + padding, bottomRight.y + padding))
}

object AScreenRect {
  def apply(rect: ARect, zoom: Zoom): AScreenRect = AScreenRect(AScreenPos.fromModel(rect.topLeft, zoom), AScreenPos.fromModel(rect.bottomRight, zoom))
}
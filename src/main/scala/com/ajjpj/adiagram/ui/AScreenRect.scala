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
}

object AScreenRect {
  def apply(rect: ARect, zoom: Zoom): AScreenRect = AScreenRect(AScreenPos.fromModel(rect.topLeft, zoom), AScreenPos.fromModel(rect.bottomRight, zoom))
}
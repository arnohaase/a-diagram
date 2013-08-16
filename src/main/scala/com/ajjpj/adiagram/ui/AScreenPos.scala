package com.ajjpj.adiagram.ui

import com.ajjpj.adiagram.geometry.APoint
import javafx.scene.input.MouseEvent
import javafx.scene.Node
import scala.language.implicitConversions

/**
 * This class represents a point in screen coordinates (as opposed to APoint, which represents
 *  a point in diagram coordinates). To transform one into the other, a Zoom factor is
 *  required.
 *
 * @author arno
 */
case class AScreenPos(x: Double, y: Double) {
  def +(p: AScreenPos) = AScreenPos(x + p.x, y + p.y)
  def -(p: AScreenPos) = AScreenPos(x - p.x, y - p.y)

  def halfWayTo(p: AScreenPos) = AScreenPos((x + p.x)/2, (y + p.y)/2)

  def distanceTo(p: AScreenPos) = Math.sqrt((x-p.x)*(x-p.x) * (y-p.y)*(y-p.y))

  def containedByNode(n: Node) = n.contains(x, y)
  def toModel (implicit zoom: Zoom) = APoint(x/zoom.factor, y/zoom.factor)
}

object AScreenPos {
  implicit def apply(p: (Double, Double)): AScreenPos = AScreenPos(p._1, p._2)

  def fromMouseEvent(evt: MouseEvent) = AScreenPos(evt.getX, evt.getY)
  def fromModel(p: APoint, zoom: Zoom) = AScreenPos(p.x * zoom.factor, p.y * zoom.factor)
}
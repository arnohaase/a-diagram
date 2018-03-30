package com.ajjpj.adiagram.render

import javafx.scene.Node
import javafx.scene.canvas.{Canvas, GraphicsContext}
import javafx.scene.image.Image

import com.ajjpj.adiagram.geometry._


trait Renderable {
  def pos: Vector2
//  def renderBounds(m2s: Model2Screen): RectShape
  def render(m2s: Model2Screen): RenderedItem
}

case class ScreenPos(x: Double, y: Double) {
  def +(p: ScreenPos) = ScreenPos(x + p.x, y + p.y)
  def -(p: ScreenPos) = ScreenPos(x - p.x, y - p.y)
}

/**
  * This is the conversion factor metric units (used at the model layer) to the Double used by Java FX
  */
class Model2Screen (val factorFromPt: Double) extends AnyVal {
  import com.ajjpj.adiagram.geometry.LenUnit._
  def toJavaFx(l: Length): Double = {
    val a = l.inUnit(pt).l
    val b = a * factorFromPt
    l.inUnit(pt).l * factorFromPt
  }
  def toJavaFx(l: Double, unit: LenUnit): Double = unit.convertTo(l, pt) * factorFromPt

  def apply(l: Length): Double = toJavaFx(l)
  def apply(l: Double, unit: LenUnit): Double = toJavaFx(l, unit)

  def toScreenCoordinates(p: Vector2, offset: Vector2) = {
    val raw = p - offset
    ScreenPos(toJavaFx(raw.x, raw.unit), toJavaFx(raw.y, raw.unit))
  }

  def singlePixel = Length(1/factorFromPt, LenUnit.pt)

  /**
    * This is the amount that two geometric shapes sharing a boundary need to overlap in order to leave no gap. TODO this may profit from experimentation
    */
  def overlapPixels = Length(.5/factorFromPt, LenUnit.pt)

  override def toString = s"Model2Screen{$factorFromPt}"
}

/**
  * This is the result of a rendering operation of a shape. To save memory (and improve cacheability), it does
  *  not contain the entire canvas but rather an arbitrary part of it plus a coordinate offset for rendering. Rendering
  *  a shape may produce a second image holding its shadow - they are stored separately because during composition of
  *  a diagram, all shadows are rendered 'behind' all regular shapes.
  *
  * @param topLeftPos holds the coordinates of the top left corner of this PartialImage relative to the total canvas
  * @param topLeftShadowPos is the same but for the 'shadow' image. If shadow is [[None]], this value holds no meaning
  */
case class RenderedItem (topLeftPos: ScreenPos, img: Image, topLeftShadowPos: ScreenPos, shadow: Option[Image])

object RenderedItem {
  /**
    * The second parameter is the offset of the top left corner of the GC in model coordinates, i.e. rendering code
    *  should subtract this vector from all coordinates before transforming them to JavaFX coordinates
    */
  type PartialImageRenderCallback = (GraphicsContext, Vector2) => Unit

  /**
    * @param bounds of the actual image without shadow - this is used to allocate the canvas. The PartialImage containing
    *               the shadow (if any) can and usually will be larger.
    */
  def fromGc(pos: Vector2, bounds: RectShape, m2s: Model2Screen, shadowStyle: Option[ShadowStyle], callback: PartialImageRenderCallback): RenderedItem = {
    // one pixel on each side for sub-pixel bleeding
    val offset = bounds.topLeft - Vector2(1, 1, LenUnit.pt) / m2s.factorFromPt
    val canvas = new Canvas (m2s(bounds.width) + 2, m2s(bounds.height) + 2)
    callback(canvas.getGraphicsContext2D, offset)

    fromNode(pos, m2s, shadowStyle, canvas)
//    val img = RenderHelper.snapshot(canvas)
//
//    val screenPos = m2s.toScreenCoordinates(offset, pos)
//    shadowStyle match {
//      case Some(s) => RenderedItem(screenPos, img, screenPos - s.additionalSpaceTopLeft(m2s), Some(s.shadow(img, m2s)))
//      case None    => RenderedItem(screenPos, img, screenPos, None)
//    }
  }

  def fromNode(pos: Vector2, m2s: Model2Screen, shadowStyle: Option[ShadowStyle], node: Node) = {
    val img = RenderHelper.snapshot(node)

    val screenPos = m2s.toScreenCoordinates(Vector2.ZERO, pos)
    shadowStyle match {
      case Some(s) => RenderedItem(screenPos, img, screenPos - s.additionalSpaceTopLeft(m2s), Some(s.shadow(img, m2s)))
      case None    => RenderedItem(screenPos, img, screenPos, None)
    }
  }
}

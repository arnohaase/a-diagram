package com.ajjpj.adiagram.render

import javafx.scene.canvas.{Canvas, GraphicsContext}
import javafx.scene.image.Image

import com.ajjpj.adiagram.geometry._


trait Renderable {
  def pos: Vector2
  def renderBounds(m2s: Model2Screen): RectShape
  def render(m2s: Model2Screen): PartialImageWithShadow
}

case class ScreenPos(x: Double, y: Double)

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
    * This is the amount that two geometric shapes with odd angles need to overlap in order to leave no gap. TODO this may profit from experimentation
    */
  def overlapPixels = Length(.5/factorFromPt, LenUnit.pt)

  override def toString = s"PointsToJavaFx{$factorFromPt}"
}

/**
  * This is the result of a rendering operation of a shape. To save memory (and improve cacheability), it does
  *  not contain the entire canvas but rather an arbitrary part of it plus a coordinate offset for rendering.
  *
  * @param renderOffset are the coordinates of the top left corner of this PartialImage relative to the total canvas
  */
case class PartialImage(renderOffset: Vector2, img: Image)
case class PartialImageWithShadow(shape: PartialImage, shadow: Option[PartialImage])

object PartialImage {
  /**
    * The second parameter is the offset of the top left corner of the GC in model coordinates, i.e. rendering code
    *  should subtract this vector from all coordinates before transforming them to JavaFX coordinates
    */
  type PartialImageRenderCallback = (GraphicsContext, Vector2) => Unit

  trait ShadowStyle { //TODO shadow
    def withShadowOffset(offset: Vector2): Vector2
    def shadow(img: Image, m2s: Model2Screen): Image
  }

  /**
    * @param bounds of the actual image without shadow - this is used to allocate the canvas. The PartialImage containing
    *               the shadow (if any) can and usually will be larger.
    */
  def fromGc(pos: Vector2, bounds: RectShape, m2s: Model2Screen, shadowStyle: Option[ShadowStyle], callback: PartialImageRenderCallback): PartialImageWithShadow = {
    // one pixel on each side for sub-pixel bleeding
    val offset = bounds.topLeft - Vector2(1, 1, LenUnit.pt) / m2s.factorFromPt
    val canvas = new Canvas (m2s(bounds.width) + 2, m2s(bounds.height) + 2)
    callback(canvas.getGraphicsContext2D, offset)

    val img = RenderHelper.snapshot(canvas)

    shadowStyle match {
      case Some(s) => PartialImageWithShadow(new PartialImage(offset - pos, img), Some(new PartialImage(s.withShadowOffset(offset) - pos, s.shadow(img, m2s))))
      case None    => PartialImageWithShadow(new PartialImage(offset - pos, img), None)
    }
  }
}




package com.ajjpj.adiagram_.render.base

import javafx.scene.canvas.{Canvas, GraphicsContext}
import javafx.scene.paint.{Color, Paint}
import javafx.scene.effect.{Shadow, BlurType}
import javafx.geometry.{VPos, Insets}
import javafx.scene.image.Image
import com.ajjpj.adiagram_.render.RenderHelper
import com.ajjpj.adiagram_.geometry.APoint
import javafx.scene.text.TextAlignment
import com.ajjpj.adiagram_.ui.Zoom


case class FillStyle (paint: Paint) {
  def applyTo(gc: GraphicsContext) {
    gc.setFill(paint)
  }
}

case class LineStyle(paint: Paint, widthNoZoom: Double) {
  def width(zoom: Zoom) = widthNoZoom * zoom.factor
  def applyTo(gc: GraphicsContext) {
    gc.setStroke(paint)
  }
}

case class ShadowStyle(offsetX: Double, offsetY: Double, radius: Double, blurType: BlurType, color: Color) {
  lazy val insets = {
    import Math._
    val top    = max(0, radius - offsetY)
    val right  = max(0, radius + offsetX)
    val bottom = max(0, radius + offsetY)
    val left   = max(0, radius - offsetX)
    new Insets (top, right, bottom, left)
  }

  def shadow(img: Image, zoom: Zoom): Image = {
    // this assumes that the underlying Image has sufficient unused space around the edge to allow
    //  for sub-pixel bleed - so we do not add one pixel around the edges here
    val width = img.getWidth + zoom.factor*(insets.getLeft + insets.getRight)
    val height = img.getHeight + zoom.factor*(insets.getTop + insets.getBottom)

    val canvas = new Canvas(width, height)
    val gc = canvas.getGraphicsContext2D

    gc.drawImage(img, zoom.factor*(insets.getLeft + offsetX), zoom.factor*(insets.getTop + offsetY))

    val shadow = new Shadow()
    shadow.setRadius(radius * zoom.factor)
    shadow.setColor(color)
    shadow.setBlurType(blurType)
    gc.applyEffect(shadow)

    RenderHelper.snapshot(canvas)
  }

  def withShadowOffset(p: APoint) = APoint(p.x - insets.getLeft, p.y - insets.getTop)
}

case class TextStyle(fontSizeInPixelsNoZoom: Double, textAlignment: TextAlignment, vpos: VPos) {
  //TODO font face

  def height(zoom: Zoom) = fontSizeInPixelsNoZoom * zoom.factor

  def applyTo(gc: GraphicsContext, zoom: Zoom) {
    val font = RenderHelper.font(height(zoom))
    gc.setFill(Color.BLACK) //TODO move this to TextStyle? UseFillStyle? Have a FillStyle in TextStyle?
    gc.setFont(font)
  }
}

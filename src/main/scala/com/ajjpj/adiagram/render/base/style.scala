package com.ajjpj.adiagram.render.base

import javafx.scene.canvas.{Canvas, GraphicsContext}
import javafx.scene.paint.{Color, Paint}
import javafx.scene.effect.{Shadow, BlurType}
import javafx.geometry.{VPos, Insets}
import javafx.scene.image.Image
import com.ajjpj.adiagram.render.RenderHelper
import com.ajjpj.adiagram.geometry.APoint
import javafx.scene.text.TextAlignment
import com.ajjpj.adiagram.ui.fw.Digest


case class FillStyle (paint: Paint) {
  def applyTo(gc: GraphicsContext) {
    gc.setFill(paint)
  }
}

case class LineStyle(paint: Paint, width: Double) {
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

  def shadow(img: Image)(implicit digest: Digest): Image = {
    // this assumes that the underlying Image has sufficient unused space around the edge to allow
    //  for sub-pixel bleed - so we do not add one pixel around the edges here
    val width = img.getWidth() + insets.getLeft() + insets.getRight()
    val height = img.getHeight() + insets.getTop() + insets.getBottom()

    val canvas = new Canvas(width, height)
    val gc = canvas.getGraphicsContext2D()

    gc.drawImage(img, insets.getLeft() + offsetX, insets.getTop() + offsetY)

    val shadow = new Shadow()
    shadow.setRadius(radius)
    shadow.setColor(color)
    shadow.setBlurType(blurType)
    gc.applyEffect(shadow)

    RenderHelper.snapshot(canvas)
  }

  def withShadowOffset(p: APoint) = APoint(p.x - insets.getLeft(), p.y - insets.getTop())
}

case class TextStyle(fontSizeInPixels: Double, textAlignment: TextAlignment, vpos: VPos) {
  //TODO font face
  val font = RenderHelper.font(fontSizeInPixels)

  def applyTo(gc: GraphicsContext) {
    gc.setFill(Color.BLACK) //TODO move this to TextStyle? UseFillStyle? Have a FillStyle in TextStyle?
    gc.setFont(font)
  }
}

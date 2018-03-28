package com.ajjpj.adiagram.render

import javafx.geometry.Insets
import javafx.scene.canvas.{Canvas, GraphicsContext}
import javafx.scene.effect.{BlurType, Shadow}
import javafx.scene.image.Image
import javafx.scene.paint.{Color, Paint}

import com.ajjpj.adiagram.geometry.{Length, Vector2}


case class LineStyle (width: Length, paint: Paint) {
  def applyTo(gc: GraphicsContext, m2s: Model2Screen): Unit = {
    gc.setStroke (paint)
    gc.setLineWidth (m2s(width))
    gc.setFill (paint)
  }
}

case class ShadowStyle(offset: Vector2, radius: Length, blurType: BlurType, color: Color)(implicit m2s: Model2Screen) {
  private val insets = {
    import Math._
    val top = max (0, m2s (radius - offset.yLen))
    val right = max (0, m2s (radius + offset.xLen))
    val bottom = max (0, m2s (radius + offset.yLen))
    val left = max (0, m2s (radius - offset.xLen))
    new Insets (top, right, bottom, left)
  }

  val additionalSpaceTopLeft = ScreenPos(insets.getLeft, insets.getTop)
  def shadow(img: Image, m2s: Model2Screen): Image = {
    // this assumes that the underlying Image has sufficient space around the edge to allow
    //  for sub-pixel bleed - so we do not add one pixel around the edges here
    val width = img.getWidth + insets.getLeft + insets.getRight
    val height = img.getHeight + insets.getTop + insets.getBottom

    val canvas = new Canvas(width, height)
    val gc = canvas.getGraphicsContext2D

    gc.drawImage(img, insets.getLeft + m2s(offset.xLen), insets.getTop + m2s(offset.yLen))

    val shadow = new Shadow()
    shadow.setRadius(m2s(radius))
    shadow.setColor(color)
    shadow.setBlurType(blurType)
    gc.applyEffect(shadow)

    RenderHelper.snapshot(canvas)
  }
}


case class RectStyle(fill: Paint, shadow: ShadowStyle)

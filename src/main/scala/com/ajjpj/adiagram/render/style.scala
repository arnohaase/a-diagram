package com.ajjpj.adiagram.render

import javafx.geometry.Insets
import javafx.scene.canvas.{Canvas, GraphicsContext}
import javafx.scene.effect.{BlurType, Shadow}
import javafx.scene.image.Image
import javafx.scene.paint.{Color, Paint}
import javafx.scene.text.TextAlignment
import com.ajjpj.adiagram.geometry.{Angle, Length, Vector2}
import com.ajjpj.adiagram.render.TextAtomStyle.{UnderlineKind, UnderlineNone}


case class LineStyle (width: Length, paint: Paint) {
  def applyTo(gc: GraphicsContext, m2s: Model2Screen): Unit = {
    gc.setStroke (paint)
    gc.setLineWidth (m2s(width))
    gc.setFill (paint)
  }
}

case class ShadowStyle(offset: Vector2, radius: Length, blurType: BlurType, color: Color) {
  private def _insets(m2s: Model2Screen) = {
    import Math._
    val top = max (0, m2s (radius - offset.yLen))
    val right = max (0, m2s (radius + offset.xLen))
    val bottom = max (0, m2s (radius + offset.yLen))
    val left = max (0, m2s (radius - offset.xLen))
    new Insets (top, right, bottom, left)
  }

  def additionalSpaceTopLeft(m2s: Model2Screen) = {
    val insets = _insets(m2s)
    ScreenPos(insets.getLeft, insets.getTop)
  }
  def shadow(img: Image, m2s: Model2Screen): Image = {
    val insets = _insets(m2s)
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

//---------------------------------------------------------------------------------- text -------------------------------------------------------------

/**
  * @param fontFamily is used instead to allow typesetting logic to be in a single place --> superscript / subscript move text up or down and modify font size
  * @param fontSize is the 'base' font size in pt - style attributes may calculate a different font size during typesetting based on it
  */
case class TextAtomStyle(fontFamily: String,
                         fontSize: Length,
                         fill: Paint,
                         italics: Boolean = false,
                         bold: Boolean = false,
                         underline: UnderlineKind = UnderlineNone,
                         strikeThrough: Boolean = false
                        )
object TextAtomStyle {
  sealed trait UnderlineKind
  case object UnderlineNone extends UnderlineKind
  case object UnderlineSingle extends UnderlineKind
  case object UnderlineDouble extends UnderlineKind
}


case class TextParagraphStyle(hAlignment: TextAlignment, lineSpacing: Length = Length.ZERO)

case class TextStyle(paragraphSpacing: Length = Length.ZERO, angle: Angle = TextStyle.angleHorizontal)
object TextStyle {
  val angleHorizontal = new Angle(Math.PI/2)
}
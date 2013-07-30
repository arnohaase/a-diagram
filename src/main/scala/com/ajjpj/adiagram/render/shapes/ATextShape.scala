package com.ajjpj.adiagram.render.shapes

import com.ajjpj.adiagram.geometry.{APoint, ARect}
import com.ajjpj.adiagram.render.base.{PartialImageWithShadow, PartialImage, TextStyle}
import com.ajjpj.adiagram.geometry.transform.Translation
import javafx.scene.canvas.GraphicsContext
import javafx.geometry.VPos
import javafx.scene.text.TextAlignment
import com.ajjpj.adiagram.ui.fw.Digest

/**
 * @author arno
 */
class ATextShape(val bounds: ARect, text: String, textStyle: TextStyle) extends AShape {
  //TODO generalize - different parts of the propText may be formatted differently, propText may be multi-line, ...

  def render(implicit digest: Digest): PartialImageWithShadow = PartialImage.fromGc(bounds.topLeft, bounds, None, (gc, t) => {
    render(gc, t)
  })

  def render(gc: GraphicsContext, t: Translation) {
    textStyle.applyTo(gc)
    gc.setTextBaseline(VPos.BOTTOM)

    gc.setTextAlign(textStyle.textAlignment)
    val tb = t(bounds)
    val renderPos = APoint(renderX(tb), renderY(tb))
    gc.fillText(text, renderPos.x, renderPos.y)
  }

  def renderY(tb: ARect) = textStyle.vpos match {
    case VPos.TOP    => tb.topLeft.y + textStyle.fontSizeInPixels
    case VPos.CENTER => tb.topLeft.y + tb.height/2 + textStyle.fontSizeInPixels/2
    case VPos.BOTTOM => tb.topLeft.y + tb.height
    case _ => throw new IllegalArgumentException("only TOP, CENTER and BOTTOM are supported")
  }

  def renderX(tb: ARect) = textStyle.textAlignment match {
    case TextAlignment.LEFT   => tb.topLeft.x
    case TextAlignment.CENTER => (tb.topLeft halfWayTo tb.topRight).x
    case TextAlignment.RIGHT  => tb.topRight.x
    case _ => throw new IllegalArgumentException("JUSTIFY not supported yet") //TODO justify
  }
}



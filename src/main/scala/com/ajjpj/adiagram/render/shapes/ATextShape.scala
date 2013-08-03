package com.ajjpj.adiagram.render.shapes

import com.ajjpj.adiagram.geometry.ARect
import com.ajjpj.adiagram.render.base.{PartialImage, TextStyle}
import com.ajjpj.adiagram.geometry.transform.Translation
import javafx.scene.canvas.GraphicsContext
import javafx.geometry.VPos
import javafx.scene.text.TextAlignment
import com.ajjpj.adiagram.ui.{AScreenPos, AScreenRect, Zoom}

/**
 * @author arno
 */
class ATextShape(val bounds: ARect, text: String, textStyle: TextStyle) extends AShape {
  //TODO generalize - different parts of the propText may be formatted differently, propText may be multi-line, ...

  def render(zoom: Zoom) = PartialImage.fromGc(bounds.topLeft, bounds, zoom, None, (gc, t) => {
    render(gc, t, zoom)
  })

  def render(gc: GraphicsContext, t: Translation, zoom: Zoom) {
    textStyle.applyTo(gc, zoom)
    gc.setTextBaseline(VPos.BOTTOM)

    gc.setTextAlign(textStyle.textAlignment)
    val tb = AScreenRect (t(bounds), zoom)
    val renderPos = AScreenPos(renderX(tb), renderY(tb, zoom))
    gc.fillText(text, renderPos.x, renderPos.y)
  }

  private def renderY(tb: AScreenRect, zoom: Zoom) = textStyle.vpos match {
    case VPos.TOP    => tb.topLeft.y + textStyle.height(zoom)
    case VPos.CENTER => tb.topLeft.y + tb.height/2 + textStyle.height(zoom)/2
    case VPos.BOTTOM => tb.topLeft.y + tb.height
    case _ => throw new IllegalArgumentException("only TOP, CENTER and BOTTOM are supported")
  }

  def renderX(tb: AScreenRect) = textStyle.textAlignment match {
    case TextAlignment.LEFT   => tb.topLeft.x
    case TextAlignment.CENTER => (tb.topLeft halfWayTo tb.topRight).x
    case TextAlignment.RIGHT  => tb.topRight.x
    case _ => throw new IllegalArgumentException("JUSTIFY not supported yet") //TODO justify
  }
}



package com.ajjpj.adiagram.render.shapes

import com.ajjpj.adiagram.geometry.ARect
import com.ajjpj.adiagram.render.base._
import javafx.scene.paint.Color
import com.ajjpj.adiagram.render.base.TextStyle
import com.ajjpj.adiagram.render.base.ShadowStyle
import scala.Some
import com.ajjpj.adiagram.render.base.FillStyle
import com.ajjpj.adiagram.ui.fw.Digest
import com.ajjpj.adiagram.ui.{Zoom, AScreenRect}

/**
 * @author arno
 */
class ABoxShape(val bounds: ARect, text: Option[String], fillStyle: FillStyle, shadowStyle: ShadowStyle, textStyle: TextStyle) extends AShape {
  override def renderBounds = bounds withPadding shadowStyle.insets

  override def render(zoom: Zoom) = PartialImage.fromGc(bounds.topLeft, bounds, zoom, Some(shadowStyle), (gc, t) => {
    fillStyle.applyTo(gc)

    val lineStyle = LineStyle(Color.BLACK, 2) //TODO make configurable

    // reduce by half a line width --> 'strokeRoundRect' only supports StrokeType.CENTER (implicitly)
    val paintBounds = AScreenRect(t(bounds) withPadding -lineStyle.widthNoZoom/2, zoom)

    val topLeft = paintBounds.topLeft
    gc.fillRoundRect(topLeft.x, topLeft.y, paintBounds.width, paintBounds.height, 25*zoom.factor, 25*zoom.factor) //TODO corner radius configurable

    lineStyle.applyTo(gc)
    gc.setLineWidth(lineStyle.width(zoom))
    gc.strokeRoundRect(topLeft.x, topLeft.y, paintBounds.width, paintBounds.height, 25*zoom.factor, 25*zoom.factor)

    text match {
      case Some(txt) =>  new ATextShape(bounds, txt, textStyle).render(gc, t, zoom)
      case None =>
    }
  })
}


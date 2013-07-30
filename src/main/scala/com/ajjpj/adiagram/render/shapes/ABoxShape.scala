package com.ajjpj.adiagram.render.shapes

import com.ajjpj.adiagram.geometry.ARect
import com.ajjpj.adiagram.render.base._
import javafx.scene.paint.Color
import com.ajjpj.adiagram.render.base.TextStyle
import com.ajjpj.adiagram.render.base.ShadowStyle
import scala.Some
import com.ajjpj.adiagram.render.base.FillStyle
import com.ajjpj.adiagram.ui.fw.Digest

/**
 * @author arno
 */
class ABoxShape(val bounds: ARect, text: Option[String], fillStyle: FillStyle, shadowStyle: ShadowStyle, textStyle: TextStyle) extends AShape {
  def render(implicit digest: Digest): PartialImageWithShadow = PartialImage.fromGc(bounds.topLeft, bounds, Some(shadowStyle), (gc, t) => {
    fillStyle.applyTo(gc)

    val topLeft = t(bounds.topLeft)
    gc.fillRoundRect(topLeft.x, topLeft.y, bounds.width, bounds.height, 25, 25) //TODO corner radius configurabor

    gc.setStroke(Color.BLACK) // line style
    gc.setLineWidth(2)
    gc.strokeRoundRect(topLeft.x, topLeft.y, bounds.width, bounds.height, 25, 25)

    text match {
      case Some(txt) =>  new ATextShape(bounds, txt, textStyle).render(gc, t)
      case None =>
    }
  })
}


package com.ajjpj.adiagram.render.text

import javafx.scene.canvas.GraphicsContext

import com.ajjpj.adiagram.geometry.{LenUnit, Length, RectShape, Vector2}
import com.ajjpj.adiagram.render.{Model2Screen, Renderable, RenderedItem, ShadowStyle}


/**
  * @param logicalTopLeft refers to the top left corner in the text's orientation, which differs from screen orientation if the text
  *                   is rotated. If the text is e.g. rotated by 180 degrees, 'top left' is actually bottom right on the
  *                   screen.
  */
case class RenderableText(logicalTopLeft: Vector2, maxWidth: Length, text: TextModel, shadowStyle: Option[ShadowStyle]) extends Renderable {
  override def pos = logicalTopLeft //NB: for rotated text, the img may go above and / or to the left of this pos

  override def render (m2s: Model2Screen) = {
    val typesetText = new FixedWidthTypeSetter(maxWidth, m2s, text).build()

    //TODO rotated text
    //TODO pos for non-left alignment
    val canvasBounds = RectShape(m2s.fromScreenCoordinates(typesetText.xMin, 0), m2s.fromScreenCoordinates(typesetText.xMax, typesetText.height))
    RenderedItem.fromGc(pos, canvasBounds, m2s, shadowStyle, (gc, t) => paint(gc, t, typesetText, m2s))
  }

  def paint(gc: GraphicsContext, t: Vector2, typesetText: TypesetText, m2s: Model2Screen): Unit = {
    val offset = m2s.toScreenCoordinates(Vector2.ZERO, t)

    for (line <- typesetText.lines;
         atom <- line.atoms
    ) {
      gc.setFont(atom.font)
      gc.setFill(atom.fill)
      gc.fillText(atom.text, atom.offsetX + offset.x, line.offsetY + atom.baselineY + offset.y)
    }
  }
}

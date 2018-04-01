package com.ajjpj.adiagram.render.text

import com.ajjpj.adiagram.geometry.{Length, RectShape, Vector2}
import com.ajjpj.adiagram.render.TextAtomStyle.{UnderlineDouble, UnderlineNone, UnderlineSingle}
import com.ajjpj.adiagram.render.{Model2Screen, Renderable, RenderedItem, ShadowStyle}
import javafx.scene.canvas.GraphicsContext
import javafx.scene.shape.StrokeLineCap


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

      def lineWidth = atom.font.getSize / 30
      def drawLine(y: Double): Unit = {
        gc.setStroke(atom.fill)
        gc.setLineWidth(lineWidth)
        gc.setLineCap(StrokeLineCap.BUTT)

        val x0 = atom.offsetX + offset.x
        gc.strokeLine(x0 - m2s.overlapPixels, y, x0 + atom.width + m2s.overlapPixels, y)
      }

      atom.underline match {
        case UnderlineNone =>
        case UnderlineSingle =>
          drawLine (line.offsetY + atom.baselineY + offset.y + lineWidth * 2.5)
        case UnderlineDouble =>
          drawLine (line.offsetY + atom.baselineY + offset.y + lineWidth * 2.5)
          drawLine (line.offsetY + atom.baselineY + offset.y + lineWidth * 4.5)
      }
      if (atom.strikeThrough) {
        drawLine (line.offsetY + atom.baselineY + offset.y - atom.font.getSize * .2)
      }
    }
  }
}

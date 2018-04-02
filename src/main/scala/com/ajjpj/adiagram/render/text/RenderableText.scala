package com.ajjpj.adiagram.render.text

import com.ajjpj.adiagram.geometry._
import com.ajjpj.adiagram.render.TextAtomStyle.{UnderlineDouble, UnderlineNone, UnderlineSingle}
import com.ajjpj.adiagram.render._
import javafx.scene.canvas.GraphicsContext
import javafx.scene.shape.StrokeLineCap
import javafx.scene.transform.{Affine, Rotate}


/**
  * @param logicalTopLeft refers to the top left corner in the text's orientation, which differs from screen orientation if the text
  *                   is rotated. If the text is e.g. rotated by 180 degrees, 'top left' is actually bottom right on the
  *                   screen.
  */
case class RenderableText(logicalTopLeft: Vector2, maxWidth: Length, text: TextModel, shadowStyle: Option[ShadowStyle]) extends Renderable {
  override def pos = logicalTopLeft //NB: for rotated text, the img may go above and / or to the left of this pos

  override def render (m2s: Model2Screen) = {
    val typesetText = new FixedWidthTypeSetter(maxWidth, m2s, text).build()

    //TODO pos for non-left alignment

    val canvasBounds = bounds(text.style.angle, typesetText.xMin, typesetText.xMax, typesetText.height, m2s)
//    val canvasBounds = RectShape(m2s.fromScreenCoordinates(typesetText.xMin, 0), m2s.fromScreenCoordinates(typesetText.xMax, typesetText.height))
    RenderedItem.fromGc(pos, canvasBounds, m2s, shadowStyle, (gc, t) => paint(gc, t, typesetText, m2s))
  }

  def bounds(angle: Angle, xMin: Double, xMax: Double, height: Double, m2s: Model2Screen) = {
    val rotation = Matrix2.rotate(angle.rad)

    val corners = Vector(
      m2s.fromScreenCoordinates(xMin, 0),
      m2s.fromScreenCoordinates(xMax, 0),
      m2s.fromScreenCoordinates(xMin, height),
      m2s.fromScreenCoordinates(xMax, height)
    )

    val transformedCorners = corners.map (rotation * _)

    val xMinNew = transformedCorners.view.map(_.x).min
    val xMaxNew = transformedCorners.view.map(_.x).max

    val yMinNew = transformedCorners.view.map(_.y).min
    val yMaxNew = transformedCorners.view.map(_.y).max

    RectShape(Vector2(xMinNew, yMinNew, corners.head.unit), Vector2(xMaxNew, yMaxNew, corners.head.unit))
  }

  def paint(gc: GraphicsContext, t: Vector2, typesetText: TypesetText, m2s: Model2Screen): Unit = {
    val origin = m2s.toScreenCoordinates(Vector2.ZERO, t)

    gc.setTransform(new Affine(new Rotate(-text.style.angle.degrees, origin.x, origin.y)))
//    gc.rotate(- text.style.angle.degrees)

    for (line <- typesetText.lines;
         atom <- line.atoms
    ) {
      gc.setFont(atom.font)
      gc.setFill(atom.fill)
      gc.fillText(atom.text, atom.offsetX + origin.x, line.offsetY + atom.baselineY + origin.y)

      def lineWidth = atom.font.getSize / 30
      def drawLine(y: Double): Unit = {
        gc.setStroke(atom.fill)
        gc.setLineWidth(lineWidth)
        gc.setLineCap(StrokeLineCap.BUTT)

        val x0 = atom.offsetX + origin.x
        gc.strokeLine(x0 - m2s.overlapPixels, y, x0 + atom.width + m2s.overlapPixels, y)
      }

      atom.underline match {
        case UnderlineNone =>
        case UnderlineSingle =>
          drawLine (line.offsetY + atom.baselineY + origin.y + lineWidth * 2.5)
        case UnderlineDouble =>
          drawLine (line.offsetY + atom.baselineY + origin.y + lineWidth * 2.5)
          drawLine (line.offsetY + atom.baselineY + origin.y + lineWidth * 4.5)
      }
      if (atom.strikeThrough) {
        drawLine (line.offsetY + atom.baselineY + origin.y - atom.font.getSize * .2)
      }
    }
  }
}

package com.ajjpj.adiagram.render.shapes.lineend

import javafx.scene.canvas.GraphicsContext
import com.ajjpj.adiagram.geometry.{Angle, APoint}
import com.ajjpj.adiagram.render.base.LineStyle
import com.ajjpj.adiagram.geometry.transform.Translation
import javafx.scene.shape.{StrokeLineJoin, StrokeLineCap}
import com.ajjpj.adiagram.ui.{AScreenPos, Zoom}

/**
 * This class creates a fullfilled triangle as a line end.
 * @author Thomas
 */
class FilledTriangleLineEnd(arrowLineLength: Double = 30.0, arrowAngle: Double = Math.PI/6) extends ALineEnd {
  val sinA = Math.sin(arrowAngle)

  override def shortenLengthUnzoomed(style: LineStyle) = style.widthNoZoom * 1.5 //style.widthNoZoom * .25 //TODO refine this (based on the actual angle)
  override def width(style: LineStyle, zoom: Zoom) = sinA * arrowLineLength + style.width(zoom)//TODO refine this

  override def paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: Translation, zoom: Zoom) {
    style.applyTo(gc)

    gc.setLineWidth(style.width(zoom))
    gc.setLineCap(StrokeLineCap.ROUND)
    gc.setLineJoin(StrokeLineJoin.ROUND)
    val tipRaw = t(p) + (angle, style.width(Zoom.Identity)/2)
    val tip =  AScreenPos.fromModel(tipRaw, zoom)
    val end0 = AScreenPos.fromModel(tipRaw + (angle + arrowAngle, arrowLineLength), zoom)
    val end1 = AScreenPos.fromModel(tipRaw + (angle - arrowAngle, arrowLineLength), zoom)
    gc.fillPolygon(Array(end0.x, tip.x, end1.x, end0.x), Array(end0.y, tip.y, end1.y, end0.y), 4);
  }
}



package com.ajjpj.adiagram.render.shapes.lineend

import com.ajjpj.adiagram.Zoom
import com.ajjpj.adiagram.geometry.APoint
import com.ajjpj.adiagram.geometry.AffineTransformation
import com.ajjpj.adiagram.geometry.Angle
import com.ajjpj.adiagram.render.AScreenPos
import com.ajjpj.adiagram.render.style.LineStyle
import javafx.scene.canvas.GraphicsContext
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin

data class RoundedPointedArrowLineEnd(val arrowLineLength: Double = 30.0, val arrowAngle: Double = Math.PI/6): ALineEnd {
    private val sinA = Math.sin(arrowAngle)

    override fun shortenLengthUnzoomed(style: LineStyle) = style.widthNoZoom / sinA * .75
    override fun width(style: LineStyle, zoom: Zoom) = sinA * arrowLineLength * zoom.factor + style.width(zoom)

    override fun paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: AffineTransformation, zoom: Zoom) {
        style.applyTo(gc)
        gc.lineWidth = style.width(zoom)
        gc.lineCap = StrokeLineCap.ROUND
        gc.lineJoin = StrokeLineJoin.MITER

        val tipRaw: APoint = t(p) + APoint(angle, style.width(Zoom.Identity) / 2 / sinA)
        val tip  = AScreenPos.fromModel(tipRaw, zoom)
        val end0 = AScreenPos.fromModel(tipRaw + APoint(angle + arrowAngle, arrowLineLength), zoom)
        val end1 = AScreenPos.fromModel(tipRaw + APoint(angle - arrowAngle, arrowLineLength), zoom)
        gc.strokePolyline(doubleArrayOf(end0.x, tip.x, end1.x), doubleArrayOf(end0.y, tip.y, end1.y), 3)
    }
}

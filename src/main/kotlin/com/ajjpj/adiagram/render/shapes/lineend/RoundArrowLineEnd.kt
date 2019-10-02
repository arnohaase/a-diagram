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

data class RoundArrowLineEnd(val arrowLineLength: Double = 30.0, val arrowAngle: Double = Math.PI/6): ALineEnd {
    private val sinA = Math.sin(arrowAngle)

    override fun shortenLengthUnzoomed(style: LineStyle) = style.widthNoZoom * 1.5 //style.widthNoZoom * .25 //TODO refine this (based on the actual angle)
    override fun width(style: LineStyle, zoom: Zoom) = sinA * arrowLineLength + style.width(zoom)//TODO refine this

    override fun paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: AffineTransformation, zoom: Zoom) {
        style.applyTo(gc)

        gc.lineWidth = style.width(zoom)
        gc.lineCap = StrokeLineCap.ROUND
        gc.lineJoin = StrokeLineJoin.ROUND

        val tipRaw = t(p) + APoint(angle, style.width(Zoom.Identity)/2)
        val tip =  AScreenPos.fromModel(tipRaw, zoom)
        val end0 = AScreenPos.fromModel(tipRaw + APoint(angle + arrowAngle, arrowLineLength), zoom)
        val end1 = AScreenPos.fromModel(tipRaw + APoint(angle - arrowAngle, arrowLineLength), zoom)

        gc.strokePolyline(doubleArrayOf(end0.x, tip.x, end1.x), doubleArrayOf(end0.y, tip.y, end1.y), 3)
    }
}

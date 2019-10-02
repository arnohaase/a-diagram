package com.ajjpj.adiagram.render.shapes.lineend

import com.ajjpj.adiagram.Zoom
import com.ajjpj.adiagram.geometry.APoint
import com.ajjpj.adiagram.geometry.AffineTransformation
import com.ajjpj.adiagram.geometry.Angle
import com.ajjpj.adiagram.render.AScreenPos
import com.ajjpj.adiagram.render.shapes.lineend.ALineEnd.Companion.OVERLAP
import com.ajjpj.adiagram.render.style.LineStyle
import javafx.scene.canvas.GraphicsContext
import javafx.scene.shape.ArcType
import java.lang.Double.max

class SemiCircleLineEnd: ALineEnd {
    override fun shortenLengthUnzoomed(style: LineStyle) = max(.0, style.widthNoZoom / 2 - OVERLAP)
    override fun width(style: LineStyle, zoom: Zoom) = 0.0

    override fun paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: AffineTransformation, zoom: Zoom) {
        val rUnzoomed = style.width(Zoom.Identity) / 2
        val center = p + APoint(angle, rUnzoomed)
        val startAngle = angle.ccw90.screenDegrees

        val arcPoint = AScreenPos.fromModel(t(APoint(center.x - rUnzoomed, center.y - rUnzoomed)), zoom)
        gc.fillArc(arcPoint.x, arcPoint.y, style.width(zoom), style.width(zoom), startAngle, 180.0, ArcType.ROUND)
    }
}
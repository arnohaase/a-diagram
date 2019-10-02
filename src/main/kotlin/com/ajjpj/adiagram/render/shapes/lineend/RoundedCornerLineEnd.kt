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

data class RoundedCornerLineEnd(val cornerFraction: Double): ALineEnd {
    private fun cornerRadius(width: Double) = width/2 * cornerFraction

    override fun shortenLengthUnzoomed(style: LineStyle): Double = max(.0, cornerRadius(style.widthNoZoom) - OVERLAP)
    override fun width(style: LineStyle, zoom: Zoom) = 0.0

    override fun paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: AffineTransformation, zoom: Zoom) {
        val rUnzoomed = style.width(Zoom.Identity) / 2
        val crUnzoomed = cornerRadius(style.width(Zoom.Identity))
        val cr = cornerRadius(style.width(zoom))

        val center = p + APoint(angle, crUnzoomed)

        val center0 = center + APoint(angle.ccw90, rUnzoomed - crUnzoomed)
        val p0 = AScreenPos.fromModel(t(APoint(center0.x - crUnzoomed, center0.y - crUnzoomed)), zoom)
        gc.fillArc(p0.x, p0.y, 2*cr, 2*cr, angle.ccw90.screenDegrees, 90.0, ArcType.ROUND)

        val center1 = center + APoint(angle.cw90, rUnzoomed - crUnzoomed)
        val p1 = AScreenPos.fromModel(t(APoint(center1.x - crUnzoomed, center1.y - crUnzoomed)), zoom)
        gc.fillArc(p1.x, p1.y, 2*cr, 2*cr, angle.opposite.screenDegrees, 90.0, ArcType.ROUND)

        val rect0 = AScreenPos.fromModel(t(p + APoint(angle.cw90,  rUnzoomed - crUnzoomed + OVERLAP/zoom.factor)), zoom)
        val rect1 = AScreenPos.fromModel(t(p + APoint(angle.ccw90, rUnzoomed - crUnzoomed + OVERLAP/zoom.factor)), zoom)
        val rect2 = AScreenPos.fromModel(t(center + APoint(angle.ccw90, rUnzoomed - crUnzoomed + OVERLAP/zoom.factor)), zoom)
        val rect3 = AScreenPos.fromModel(t(center + APoint(angle.cw90,  rUnzoomed - crUnzoomed + OVERLAP/zoom.factor)), zoom)
        gc.fillPolygon(doubleArrayOf(rect0.x, rect1.x, rect2.x, rect3.x), doubleArrayOf(rect0.y, rect1.y, rect2.y, rect3.y), 4)
    }
}

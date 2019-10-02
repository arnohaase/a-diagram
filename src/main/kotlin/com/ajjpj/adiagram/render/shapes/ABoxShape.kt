package com.ajjpj.adiagram.render.shapes

import com.ajjpj.adiagram.Zoom
import com.ajjpj.adiagram.geometry.APoint
import com.ajjpj.adiagram.geometry.ARect
import com.ajjpj.adiagram.geometry.AffineTransformation
import com.ajjpj.adiagram.render.AScreenRect
import com.ajjpj.adiagram.render.PartialImage
import com.ajjpj.adiagram.render.style.FillStyle
import com.ajjpj.adiagram.render.style.LineStyle
import com.ajjpj.adiagram.render.style.ShadowStyle
import com.ajjpj.adiagram.render.style.TextStyle
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

data class ABoxShape(override val bounds: ARect, val text: String?, val fillStyle: FillStyle, val shadowStyle: ShadowStyle, val textStyle: TextStyle): AShape {
    override val pos: APoint
        get() = bounds.topLeft
    
    override val renderBounds = bounds.withPadding(shadowStyle.insets)

    override fun render(zoom: Zoom) = PartialImage.fromGc(bounds.topLeft, bounds, zoom, shadowStyle, fun(gc: GraphicsContext, t: AffineTransformation) {
        fillStyle.applyTo(gc)

        val lineStyle = LineStyle(Color.BLACK, 2.0) //TODO make configurable

        // reduce by half a line width --> 'strokeRoundRect' only supports StrokeType.CENTER (implicitly)
        val paintBounds = AScreenRect(t(bounds).withPadding (-lineStyle.widthNoZoom/2), zoom)

        val topLeft = paintBounds.topLeft
        gc.fillRoundRect(topLeft.x, topLeft.y, paintBounds.width, paintBounds.height, 25*zoom.factor, 25*zoom.factor) //TODO corner radius configurable

        lineStyle.applyTo(gc)
        gc.setLineWidth(lineStyle.width(zoom))
        gc.strokeRoundRect(topLeft.x, topLeft.y, paintBounds.width, paintBounds.height, 25*zoom.factor, 25*zoom.factor)

        if (text != null) {
            ATextShape(bounds, text, textStyle).render(gc, t, zoom)
        }
    })
}


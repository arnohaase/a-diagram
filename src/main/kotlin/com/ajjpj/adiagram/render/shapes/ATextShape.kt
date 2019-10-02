package com.ajjpj.adiagram.render.shapes

import com.ajjpj.adiagram.Zoom
import com.ajjpj.adiagram.geometry.APoint
import com.ajjpj.adiagram.geometry.ARect
import com.ajjpj.adiagram.geometry.AffineTransformation
import com.ajjpj.adiagram.render.AScreenPos
import com.ajjpj.adiagram.render.AScreenRect
import com.ajjpj.adiagram.render.PartialImage
import com.ajjpj.adiagram.render.style.TextStyle
import javafx.geometry.VPos
import javafx.scene.canvas.GraphicsContext
import javafx.scene.text.TextAlignment

data class ATextShape (override val bounds: ARect, val text: String, val textStyle: TextStyle): AShape {
    //TODO generalize - different parts of the propText may be formatted differently, propText may be multi-line, ...

    override val pos: APoint
        get() = bounds.topLeft

    override val renderBounds
            get() = bounds

    override fun render(zoom: Zoom) = PartialImage.fromGc(bounds.topLeft, bounds, zoom, null, fun (gc: GraphicsContext, t: AffineTransformation) {
        render(gc, t, zoom)
    })

    fun render(gc: GraphicsContext, t: AffineTransformation, zoom: Zoom) {
        textStyle.applyTo(gc, zoom)
        gc.setTextBaseline(VPos.BOTTOM)

        gc.setTextAlign(textStyle.textAlignment)
        val tb = AScreenRect (t(bounds), zoom)
        val renderPos = AScreenPos(renderX(tb), renderY(tb, zoom))
        gc.fillText(text, renderPos.x, renderPos.y)
    }

    private fun renderY(tb: AScreenRect, zoom: Zoom): Double = when (textStyle.vpos) {
        VPos.TOP    -> tb.topLeft.y + textStyle.height(zoom)
        VPos.CENTER -> tb.topLeft.y + tb.height/2 + textStyle.height(zoom)/2
        VPos.BOTTOM -> tb.topLeft.y + tb.height
        else        -> throw IllegalArgumentException("only TOP, CENTER and BOTTOM are supported")
    }

    private fun renderX(tb: AScreenRect): Double = when (textStyle.textAlignment) {
        TextAlignment.LEFT   -> tb.topLeft.x
        TextAlignment.CENTER -> (tb.topLeft.halfWayTo (tb.topRight)).x
        TextAlignment.RIGHT  -> tb.topRight.x
        else -> throw IllegalArgumentException("JUSTIFY not supported yet") //TODO justify
    }
}



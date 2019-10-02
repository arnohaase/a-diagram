package com.ajjpj.adiagram.render.style

import com.ajjpj.adiagram.Zoom
import com.ajjpj.adiagram.render.resources.FontHelper
import javafx.geometry.VPos
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment

data class TextStyle(val fontSizeInPixelsNoZoom: Double, val textAlignment: TextAlignment, val vpos: VPos) {
    //TODO font face
    //TODO color

    fun height(zoom: Zoom) = fontSizeInPixelsNoZoom * zoom.factor

    fun applyTo(gc: GraphicsContext, zoom: Zoom) {
        val font = FontHelper.font(height(zoom))
        gc.fill = Color.BLACK //TODO move this to TextStyle? UseFillStyle? Have a FillStyle in TextStyle?
        gc.font = font
    }
}
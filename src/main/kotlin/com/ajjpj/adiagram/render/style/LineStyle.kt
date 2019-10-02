package com.ajjpj.adiagram.render.style

import com.ajjpj.adiagram.Zoom
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Paint

data class LineStyle (val paint: Paint, val widthNoZoom: Double) {
    fun width(zoom: Zoom) = widthNoZoom * zoom.factor
    fun applyTo(gc: GraphicsContext) {
        gc.stroke = paint
    }
}

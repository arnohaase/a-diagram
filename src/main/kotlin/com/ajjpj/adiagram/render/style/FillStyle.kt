package com.ajjpj.adiagram.render.style

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Paint

data class FillStyle(val paint: Paint) {
    fun applyTo(gc: GraphicsContext) {
        gc.fill = paint
    }
}
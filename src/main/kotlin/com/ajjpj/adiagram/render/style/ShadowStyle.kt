package com.ajjpj.adiagram.render.style

import com.ajjpj.adiagram.Zoom
import com.ajjpj.adiagram.geometry.APoint
import com.ajjpj.adiagram.render.RenderHelper
import javafx.geometry.Insets
import javafx.scene.canvas.Canvas
import javafx.scene.effect.BlurType
import javafx.scene.effect.Shadow
import javafx.scene.image.Image
import javafx.scene.paint.Color
import java.lang.Double.max

data class ShadowStyle(val offsetX: Double, val offsetY: Double, val radius: Double, val blurType: BlurType, val color: Color) {
    val insets: Insets =
        Insets (max(.0, radius - offsetY),
                max(.0, radius + offsetX),
                max(.0, radius + offsetY),
                max(.0, radius - offsetX))

    fun shadow(img: Image, zoom: Zoom): Image {
        // this assumes that the underlying Image has sufficient unused space around the edge to allow
        //  for sub-pixel bleed - so we do not add one pixel around the edges here
        val width = img.width + zoom.factor*(insets.left + insets.right)
        val height = img.height + zoom.factor*(insets.top + insets.bottom)

        val canvas = Canvas(width, height)
        val gc = canvas.graphicsContext2D

        gc.drawImage(img, zoom.factor*(insets.left + offsetX), zoom.factor*(insets.top + offsetY))

        val shadow = Shadow()
        shadow.radius = radius * zoom.factor
        shadow.color = color
        shadow.blurType = blurType
        gc.applyEffect(shadow)

        return RenderHelper.snapshot(canvas)
    }

    fun withShadowOffset(p: APoint) = APoint(p.x - insets.left, p.y - insets.top)
}

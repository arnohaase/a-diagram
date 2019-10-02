package com.ajjpj.adiagram.render

import com.ajjpj.adiagram.Zoom
import com.ajjpj.adiagram.geometry.APoint
import com.ajjpj.adiagram.geometry.ARect
import com.ajjpj.adiagram.geometry.AffineTransformation
import com.ajjpj.adiagram.render.style.ShadowStyle
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image

/**
 * This is the result of a rendering operation of a shape. To save memory (and improve cacheability), it does
 *  not contain the entire canvas but rather an arbitrary part of it plus a coordinate offset for rendering.
 *
 * @param renderOffset are the coordinates at which the upper left corner of the image should be rendered
 *
 * @author arno
 */
class PartialImage (val renderOffset: APoint, val img: Image) {
    companion object {

        /**
         * @param bounds of the actual image (toModel shadow, if any)
         * @param shadowStyle null if there is no shadow
         */
        fun fromGc(pos: APoint, bounds: ARect, zoom: Zoom, shadowStyle: ShadowStyle?, callback: (GraphicsContext, AffineTransformation) -> Unit): PartialImageWithShadow {
            // one pixel on each side for sub-pixel bleeding
            val offset = bounds.topLeft + APoint(-1/zoom.factor, -1/zoom.factor)

            val canvas = Canvas(bounds.width*zoom.factor+2, bounds.height*zoom.factor+2)
            val t = AffineTransformation.translation(offset.inverse)
            callback(canvas.graphicsContext2D, t)

            val img = RenderHelper.snapshot(canvas)

            if(shadowStyle != null) {
                return PartialImageWithShadow(PartialImage(offset - pos, img), PartialImage(shadowStyle.withShadowOffset(offset) - pos, shadowStyle.shadow(img, zoom)))
            }
            else {
                return PartialImageWithShadow(PartialImage(offset - pos, img), null)
            }
        }
    }
}

class PartialImageWithShadow(val shape: PartialImage, val shadow: PartialImage?)

package com.ajjpj.adiagram.render.shapes.lineend

import com.ajjpj.adiagram.Zoom
import com.ajjpj.adiagram.geometry.APoint
import com.ajjpj.adiagram.geometry.AffineTransformation
import com.ajjpj.adiagram.geometry.Angle
import com.ajjpj.adiagram.render.style.LineStyle
import javafx.scene.canvas.GraphicsContext

interface ALineEnd {
    fun shortenLengthUnzoomed(style: LineStyle): Double

    fun width(style: LineStyle, zoom: Zoom): Double

    /**
     * @param p is the end of the physical line *including* the line ending (i.e. before
     *          being corrected with the result of getShortenLength())
     * @param angle direction of the line as if it was *starting* from this end
     */
    fun paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: AffineTransformation, zoom: Zoom)

    companion object {
        /**
         * The shortening of the actual line is reduced by this distance to avoid unpainted artifacts where
         * line and line end decoration meet. This may require some experimentation and cross-platform testing!
         */
        val OVERLAP = 0.5
    }
}

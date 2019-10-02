package com.ajjpj.adiagram.render.shapes.lineend

import com.ajjpj.adiagram.Zoom
import com.ajjpj.adiagram.geometry.APoint
import com.ajjpj.adiagram.geometry.AffineTransformation
import com.ajjpj.adiagram.geometry.Angle
import com.ajjpj.adiagram.render.style.LineStyle
import javafx.scene.canvas.GraphicsContext

class NullLineEnd: ALineEnd {
    override fun shortenLengthUnzoomed(style: LineStyle): Double = .0
    override fun width(style: LineStyle, zoom: Zoom): Double = .0

    override fun paint(gc: GraphicsContext, p: APoint, angle: Angle, style: LineStyle, t: AffineTransformation, zoom: Zoom) {
    }
}
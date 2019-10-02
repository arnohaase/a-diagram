package com.ajjpj.adiagram.render.shapes

import com.ajjpj.adiagram.Zoom
import com.ajjpj.adiagram.geometry.APoint
import com.ajjpj.adiagram.geometry.ARect
import com.ajjpj.adiagram.render.PartialImageWithShadow

interface AShape {
    val pos: APoint // = bounds.topLeft //TODO remove this - assume the 'pos' to always be (0, 0) (?)
    val bounds: ARect
    val renderBounds: ARect // including space for arrow heads, shadows etc.
    fun render(zoom: Zoom): PartialImageWithShadow
}

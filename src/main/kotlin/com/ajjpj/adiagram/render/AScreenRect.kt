package com.ajjpj.adiagram.render

import com.ajjpj.adiagram.Zoom
import com.ajjpj.adiagram.geometry.APoint
import com.ajjpj.adiagram.geometry.ARect
import javafx.scene.Node


/**
 * This class represents a point in screen coordinates (as opposed to APoint, which represents
 *  a point in diagram coordinates). To transform one into the other, a Zoom factor is
 *  required.
 */
data class AScreenPos(val x: Double, val y: Double) {
    operator fun plus(p: AScreenPos) = AScreenPos(x + p.x, y + p.y)
    operator fun minus(p: AScreenPos) = AScreenPos(x - p.x, y - p.y)

    fun halfWayTo(p: AScreenPos) = AScreenPos((x + p.x)/2, (y + p.y)/2)

    fun distanceTo(p: AScreenPos) = Math.sqrt((x-p.x)*(x-p.x) * (y-p.y)*(y-p.y))

    fun containedByNode(n: Node) = n.contains(x, y)
    fun toModel (zoom: Zoom) = APoint(x/zoom.factor, y/zoom.factor)

    companion object {
//        fun fromMouseEvent(evt: MouseEvent) = AScreenPos(evt.getX, evt.getY)
        fun fromModel(p: APoint, zoom: Zoom) = AScreenPos(p.x * zoom.factor, p.y * zoom.factor)
    }
}

data class AScreenRect (val topLeft: AScreenPos, val bottomRight: AScreenPos) {
    val topRight: AScreenPos
        get() = AScreenPos(bottomRight.x, topLeft.y)
    val bottomLeft: AScreenPos
        get() = AScreenPos(topLeft.x,     bottomRight.y)

    val width: Double
        get() = bottomRight.x - topLeft.x
    val height: Double
        get() = bottomRight.y - topLeft.y

    fun withPadding (padding: Double) = AScreenRect(AScreenPos(topLeft.x - padding, topLeft.y - padding), AScreenPos(bottomRight.x + padding, bottomRight.y + padding))

    fun contains(p: AScreenPos) = topLeft.x <= p.x && bottomRight.x >= p.x && topLeft.y <= p.y && bottomRight.y >= p.y

    companion object {
        operator fun invoke(topLeft: AScreenPos, width: Double, height: Double): AScreenRect = AScreenRect(topLeft, topLeft + AScreenPos(width, height))
        operator fun invoke(rect: ARect, zoom: Zoom): AScreenRect = AScreenRect(AScreenPos.fromModel(rect.topLeft, zoom), AScreenPos.fromModel(rect.bottomRight, zoom))
    }
}

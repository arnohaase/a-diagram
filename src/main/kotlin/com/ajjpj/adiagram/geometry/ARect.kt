package com.ajjpj.adiagram.geometry

import javafx.geometry.Insets

data class ARect(val topLeft: APoint, val dim: ADim): GeometricShape {
    val topRight: APoint
        get() = topLeft + APoint(dim.width, .0)
    val bottomLeft: APoint
        get() = topLeft + APoint(.0, dim.height)
    val bottomRight: APoint
        get() = topLeft + APoint(dim.width, dim.height)

    val width: Double
        get()  = dim.width
    val height: Double
        get() = dim.height

    val center: APoint
        get() = topLeft.halfWayTo(bottomRight)

    fun withPadding(padding: Double): ARect = withPadding(padding, padding)
    fun withPadding(hPadding: Double, vPadding: Double): ARect = withPadding(hPadding, vPadding, hPadding, vPadding)
    fun withPadding(leftPadding: Double, topPadding: Double, rightPadding: Double, bottomPadding: Double): ARect =
            ARect(topLeft - APoint(leftPadding, topPadding), ADim(dim.width + leftPadding + rightPadding, dim.height + topPadding + bottomPadding))
    fun withPadding(insets: Insets): ARect = withPadding (insets.left, insets.top, insets.right, insets.bottom)

    override fun contains(p: APoint) =
            topLeft.x <= p.x && topLeft.x+dim.width  >= p.x &&
            topLeft.y <= p.y && topLeft.y+dim.height >= p.y

    private fun segmentIntersectionVertical(p1: APoint, p2: APoint, x: Double, y3: Double, y4: Double): APoint? {
        if(x < Math.min(p1.x, p2.x) || x > Math.max(p1.x, p2.x)) {
            return null
        }
        else {
            val det = (p1.x - p2.x)*(y3 - y4)
            if (det == 0.0) {
                return null
            }
            else {
                val y_res = ((p1.x*p2.y - p1.y*p2.x)*(y3 - y4) - x*(p1.y - p2.y)*(y4 - y3)) / det

                if(y_res >= Math.min(y3, y4) && y_res <= Math.max(y3, y4))
                    return APoint(x, y_res)
                else
                    return null
            }
        }
    }

    private fun segmentIntersectionHorizontal(p1: APoint, p2: APoint, x3: Double, x4: Double, y: Double): APoint? {
        if(y < Math.min(p1.y, p2.y) || y > Math.max(p1.y, p2.y)) {
            return null
        }
        else {
            val det = -(x3 - x4)*(p1.y - p2.y)
            if (det == 0.0) {
                return null
            }
            else {
                val x_res = ((p1.x*p2.y - p1.y*p2.x)*(x3 - x4) - y*(p1.x - p2.x)*(x3 - x4)) / det

                if(x_res >= Math.min(x3, x4) && x_res <= Math.max(x3, x4))
                    return APoint(x_res, y)
                else
                    return null
            }
        }
    }

    override fun intersection(inside: APoint, outside: APoint): APoint =
        segmentIntersectionHorizontal (inside, outside, topLeft.x,         topLeft.x + width, topLeft.y) ?:
        segmentIntersectionHorizontal (inside, outside, topLeft.x,         topLeft.x + width, topLeft.y + height) ?:
        segmentIntersectionVertical   (inside, outside, topLeft.x,         topLeft.y        , topLeft.y + height) ?:
        segmentIntersectionVertical   (inside, outside, topLeft.x + width, topLeft.y        , topLeft.y + height) ?:
        inside // fallback

    companion object {
        operator fun invoke(p0: APoint, p1: APoint): ARect = createWithPadding(.0, p0, p1)
        operator fun invoke(p: APoint, width: Double, height: Double): ARect = ARect(p, ADim(width, height))

        fun fromCoordinates(x0: Double, y0: Double, x1: Double, y1: Double) = ARect(APoint(x0, y0), APoint(x1, y1))

        fun createWithPadding(padding: Double, p0: APoint, p1: APoint): ARect {
            val minX = Math.min(p0.x, p1.x) - padding
            val maxX = Math.max(p0.x, p1.x) + padding
            val minY = Math.min(p0.y, p1.y) - padding
            val maxY = Math.max(p0.y, p1.y) + padding

            return ARect(APoint(minX, minY), ADim(maxX-minX, maxY-minY))
        }

        fun containingRect(rects: Collection<ARect>) {
            if(rects.isEmpty()) {
                ARect(APoint.ZERO, ADim(.0, .0))
            }
            else {
                var minX = Double.MAX_VALUE
                var maxX = Double.MIN_VALUE
                var minY = Double.MAX_VALUE
                var maxY = Double.MIN_VALUE

                for (r in rects) {
                    minX = Math.min(minX, r.topLeft.x)
                    maxX = Math.max(maxX, r.bottomRight.x)
                    minY = Math.min(minY, r.topLeft.y)
                    maxY = Math.max(maxY, r.bottomRight.y)
                }

                fromCoordinates(minX, minY, maxX, maxY)
            }
        }
    }
}

package com.ajjpj.adiagram.geometry

data class APoint(val x: Double, val y: Double) {
    fun halfWayTo(p: APoint) = APoint((x+p.x)/2, (y+p.y)/2)

    val inverse: APoint
        get() = APoint(-x, -y)

    operator fun plus(p: APoint): APoint = APoint(x+p.x, y+p.y)

    operator fun minus(p: APoint): APoint = APoint(x-p.x, y-p.y)

    fun distanceTo(p: APoint) = Math.sqrt((p.x-x)*(p.x-x) + (p.y-y)*(p.y-y))

    companion object {
        val ZERO = APoint(.0, .0)

        operator fun invoke(angle: Angle, d: Double): APoint = APoint(d*angle.unitX, d*angle.unitY)
    }
}
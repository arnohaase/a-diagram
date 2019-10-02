package com.ajjpj.adiagram.geometry

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

inline class Angle(val angle: Double) {
    /** converts the angle to the JavaFX representation, i.e. degrees ccw from 'horizontal to the right' */
    val screenDegrees: Double
        get() = (this.angle * 180 / Math.PI + 270 + 360) % 360

    val unitX: Double
        get() = sin(angle)
    val unitY
        get() = cos(angle)

    val opposite
        get() = Angle(angle + Math.PI)

    val cw90
        get() = Angle(angle - Math.PI/2)
    val ccw90
        get() = Angle(angle + Math.PI/2)

    operator fun plus(other: Angle) = Angle(angle + other.angle)
    operator fun plus(a: Double) = Angle(angle + a)
    operator fun minus(other: Angle) = Angle(angle - other.angle)
    operator fun minus(a: Double) = Angle(angle - a)

    companion object {
        fun fromDxDy(dx: Double, dy: Double) = Angle(atan2(dx, dy))
        fun fromLine(p0: APoint, p1: APoint) = fromDxDy(p1.x - p0.x, p1.y - p0.y)
    }
}

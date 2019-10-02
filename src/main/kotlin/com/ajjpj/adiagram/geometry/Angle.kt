package com.ajjpj.adiagram.geometry

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class Angle(val angle: Double) {
    /** converts the angle to the JavaFX representation, i.e. degrees ccw from 'horizontal to the right' */
    fun screenDegrees() = (this.angle * 180 / Math.PI + 270 + 360) % 360

    fun unitX() = sin(angle)
    fun unitY() = cos(angle)

    fun opposite() = Angle(angle + Math.PI)

    fun cw90() = Angle(angle - Math.PI/2)
    fun ccw90() = Angle(angle + Math.PI/2)

    fun plus(other: Angle) = Angle(angle + other.angle)
    fun plus(a: Double) = Angle(angle + a)
    fun minus(other: Angle) = Angle(angle - other.angle)
    fun minus(a: Double) = Angle(angle - a)

    companion object {
        fun fromDxDy(dx: Double, dy: Double) = Angle(atan2(dx, dy))
        fun fromLine(p0: APoint, p1: APoint) = fromDxDy(p1.x - p0.x, p1.y - p0.y)
    }
}

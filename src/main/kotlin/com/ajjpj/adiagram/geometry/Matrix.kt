package com.ajjpj.adiagram.geometry

import java.lang.Double.NaN
import kotlin.math.cos
import kotlin.math.sin

data class Matrix (val m00: Double, val m10: Double, val m01: Double, val m11: Double) {
    private var _det: Double = NaN
    val det: Double
        get() {
            if (_det.isNaN()) {
                _det = m00 * m11 - m10 * m01
            }
            return _det
        }

    private var _inverse: Matrix? = null
    val inverse: Matrix
        get() = _inverse ?: calcInverse()

    private fun calcInverse(): Matrix {
        val result = Matrix(m11 / det, -m10 / det, -m01 / det, m00 / det)
        _inverse = result
        return result
    }

    operator fun times(p: APoint): APoint = APoint (m00*p.x + m01*p.y, m10*p.x + m11*p.y)
    operator fun times(o: Matrix): Matrix = Matrix(m00 * o.m00 + m01 * o.m10, m10 * o.m00 + m11 * o.m10, m00 * o.m01 + m01 * o.m11, m10 * o.m01 + m11 * o.m11)

    companion object {
        val UNIT = Matrix(1.0, 0.0, 0.0, 1.0)

        fun scale(factor: Double) = Matrix(factor, .0, .0, factor)
        fun rotate(angle: Double) = Matrix(cos(angle), -sin(angle), sin(angle), cos(angle))
    }
}



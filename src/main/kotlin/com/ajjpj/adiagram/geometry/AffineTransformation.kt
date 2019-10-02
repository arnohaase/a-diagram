package com.ajjpj.adiagram.geometry

/**
 * the semantics of the constructor data structures are as follows:
 *
 * y = M * x + t
 *
 * i.e. a point is first multiplied by the transformation matrix and then translated by the translation vector
 */
data class AffineTransformation(val translation: APoint, val m: Matrix) {
    operator fun invoke(p: APoint): APoint = m*p + translation
    operator fun invoke (rect: ARect): ARect = ARect(this(rect.topLeft), this(rect.bottomRight))

    fun inverse() = AffineTransformation(m.inverse, (m.inverse * translation).inverse)

    fun before(other: AffineTransformation) = other.after(this)
    fun after(other: AffineTransformation) = AffineTransformation(m * other.m, m * other.translation + translation)

    fun scaleFactor() = m.det

    companion object {
        operator fun invoke(m: Matrix, translation: APoint) = AffineTransformation(translation, m)
        fun translation(translation: APoint) = AffineTransformation(translation, Matrix.UNIT)

        fun scaling(origin: APoint, factor: Double) = translation(origin)
                .after(AffineTransformation(Matrix.scale(factor), APoint.ZERO))
                .after(translation(origin.inverse))

        fun rotation(origin: APoint, angle: Angle) = translation(origin)
                .after(AffineTransformation(Matrix.rotate(angle.angle), APoint.ZERO))
                .after(translation(origin.inverse))
    }
}

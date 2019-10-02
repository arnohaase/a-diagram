package com.ajjpj.adiagram.geometry

data class ADim(val width: Double, val height: Double) {
    operator fun plus(other: ADim) = ADim(width + other.width, height + other.height)
}
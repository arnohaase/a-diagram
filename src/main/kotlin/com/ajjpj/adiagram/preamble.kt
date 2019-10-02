package com.ajjpj.adiagram

inline class Zoom(val factor: Double) {
    operator fun times(scale: Double) = Zoom(factor * scale) //TODO rounding?

    companion object {
        val Identity = Zoom(1.0)
    }
}
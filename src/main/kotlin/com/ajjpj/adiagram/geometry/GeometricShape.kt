package com.ajjpj.adiagram.geometry

interface GeometricShape {
    fun contains(p: APoint): Boolean
    fun intersection(inside: APoint, outside: APoint): APoint
}
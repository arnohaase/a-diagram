package com.ajjpj.adiagram.geometry

import com.ajjpj.adiagram.ADiagramSpec


class Vector2Spec extends ADiagramSpec {
  "A Vector2" should "contain its dimensions and unit" in {
    val v = Vector2(1, 2, LenUnit.point)
    v.x shouldBe (1.0 +- eps)
    v.y shouldBe (2.0 +- eps)
    v.unit shouldBe LenUnit.point
  }

  it should "convert to another LenUnit" in {
    val conv = Vector2(1, 2, LenUnit.inch).inUnit(LenUnit.point)
    conv.x shouldBe (72.0 +- eps)
    conv.y shouldBe (144.0 +- eps)
    conv.unit shouldBe LenUnit.point
  }

  it should "calculate the half way to another Vector2" in {
    val v1 = Vector2(1, 2, LenUnit.inch)
    val v2 = Vector2(144, 288, LenUnit.point)
    val m = v1 halfWayTo v2

    m.x shouldBe (1.5 +- eps)
    m.y shouldBe (3.0 +- eps)
    m.unit shouldBe LenUnit.inch
  }

  it should "calculate its inverse" in {
    val v = Vector2(1, 2, LenUnit.mm).inverse
    v.x shouldBe (-1.0 +- eps)
    v.y shouldBe (-2.0 +- eps)
    v.unit shouldBe LenUnit.mm
  }

  it should "add another vector" in {
    val v1 = Vector2(1, 2, LenUnit.inch)
    val v2 = Vector2(144, 288, LenUnit.point)
    val s = v1 + v2
    s.x shouldBe (3.0 +- eps)
    s.y shouldBe (6.0 +- eps)
    s.unit shouldBe LenUnit.inch
  }

  it should "subtract another vector" in {
    val v1 = Vector2(5, 3, LenUnit.inch)
    val v2 = Vector2(144, 288, LenUnit.point)
    val s = v1 - v2
    s.x shouldBe (3.0 +- eps)
    s.y shouldBe (-1.0 +- eps)
    s.unit shouldBe LenUnit.inch
  }
}

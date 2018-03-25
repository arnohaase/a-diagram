package com.ajjpj.adiagram.geometry

import java.util.Random

import com.ajjpj.adiagram.ADiagramSpec


class Matrix2Spec extends ADiagramSpec {
  import Math._
  import LenUnit._

  "A Matrix2" should "transform a Vector2" in {
    val m = Matrix2(2, 3, 4, 5)

    (m * Vector2.ZERO).x should be (.0 +- eps)
    (m * Vector2.ZERO).y should be (.0 +- eps)

    val v10 = m * Vector2(1, 0, mm)
    val v01 = m * Vector2(0, 1, mm)
    val v22 = m * Vector2(2, 2, mm)

    v10.x should be (2.0 +- eps)
    v10.y should be (3.0 +- eps)
    v10.unit shouldBe mm

    v01.x should be (4.0 +- eps)
    v01.y should be (5.0 +- eps)
    v01.unit shouldBe mm

    v22.x should be (12.0 +- eps)
    v22.y should be (16.0 +- eps)
    v22.unit shouldBe mm
  }

  it should "multiply with another Matrix2" in {
    val m = Matrix2(1, 2, 3, 4) * Matrix2(5, 6, 7, 8)
    m.m00 shouldBe (23.0 +- eps)
    m.m10 shouldBe (34.0 +- eps)
    m.m01 shouldBe (31.0 +- eps)
    m.m11 shouldBe (46.0 +- eps)
  }

  it should "calculate its inverse" in {
    val rand = new Random(25)

    for(i <- 0 to 1*1000) {
      val m = Matrix2(rand.nextDouble()+2, rand.nextDouble(), rand.nextDouble(), rand.nextDouble()+2)
      val unit = m * m.inverse

      unit.m00 should be (1.0 +- eps)
      unit.m10 should be (0.0 +- eps)
      unit.m01 should be (0.0 +- eps)
      unit.m11 should be (1.0 +- eps)
    }
  }

  it should "create a scaling transformation" in {
    val v = Matrix2.scale(3) * Vector2 (2, 3, mm)
    v.x should be (6.0 +- eps)
    v.y should be (9.0 +- eps)
    v.unit shouldBe mm
  }

  it should "create a rotation" in {
    val v = Matrix2.rotate(PI/2) * Vector2(1, 0, inch)
    v.x should be ( 0.0 +- eps)
    v.y should be (-1.0 +- eps)
    v.unit shouldBe inch
  }
}

package com.ajjpj.adiagram_.geometry.transform

import java.util.Random

import com.ajjpj.adiagram_.geometry.APoint
import org.scalatest.{FunSuite, Matchers}

/**
 * @author arno
 */
class MatrixTest extends FunSuite with Matchers {
  val eps = .00000001

  test("matrix times vector") {
    val m = Matrix(2, 3, 4, 5)

    (m * APoint.ZERO).x should be (.0 +- eps)
    (m * APoint.ZERO).y should be (.0 +- eps)

    (m * (1.0, 0.0)).x should be (2.0 +- eps)
    (m * (1.0, 0.0)).y should be (3.0 +- eps)

    (m * (0.0, 1.0)).x should be (4.0 +- eps)
    (m * (0.0, 1.0)).y should be (5.0 +- eps)

    (m * (2.0, 2.0)).x should be (12.0 +- eps)
    (m * (2.0, 2.0)).y should be (16.0 +- eps)
  }

  test("matrix times matrix") {
    val m = Matrix(1, 2, 3, 4) * Matrix (5, 6, 7, 8)
    m.m00 should be (23.0 +- eps)
    m.m10 should be (34.0 +- eps)
    m.m01 should be (31.0 +- eps)
    m.m11 should be (46.0 +- eps)
  }

  test("matrix inverse") {
    val rand = new Random(25)

    for(i <- 0 to 1*1000) {
      val m = Matrix(rand.nextDouble()+2, rand.nextDouble(), rand.nextDouble(), rand.nextDouble()+2)
      val unit = m * m.inverse

      unit.m00 should be (1.0 +- eps)
      unit.m10 should be (0.0 +- eps)
      unit.m01 should be (0.0 +- eps)
      unit.m11 should be (1.0 +- eps)
    }
  }

  test("matrix scale") {
    val v = Matrix.scale(3) * (2.0, 3.0)
    v.x should be (6.0 +- eps)
    v.y should be (9.0 +- eps)
  }

  test("matrix rotate") {
    val v = Matrix.rotate(Math.PI/2) * (1.0, 0.0)
    v.x should be ( 0.0 +- eps)
    v.y should be (-1.0 +- eps)
  }
}



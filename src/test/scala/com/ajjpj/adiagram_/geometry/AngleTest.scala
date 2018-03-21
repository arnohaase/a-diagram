package com.ajjpj.adiagram_.geometry

import org.scalatest.FunSuite
import org.scalatest.Matchers


/**
 * @author arno
 */
class AngleTest extends FunSuite with Matchers {
  val eps = .00000001

  import Math._

  test("create Angle") {
    Angle(0).angle should equal (0.0)
    Angle(1).angle should equal (1.0)
  }

  test("normalize Angle") {
    Angle(3       * PI).angle should be (PI +-(eps))
    Angle(         -PI).angle should be (PI +-(eps))
    Angle(9999999 * PI).angle should be (PI +-(eps))
  }

  test("opposite Angle") {
    Angle(0).opposite.angle should be (PI +-(eps))
    Angle(1).opposite.angle should be (1.0 + PI +-(eps))
  }

  test("rotate Angle") {
    Angle(PI).cw90.angle  should be (  PI/2 +-(eps))
    Angle(PI).ccw90.angle should be (3*PI/2 +-(eps))
  }

  test("unit distance of Angle") {
    Angle(0).unitX should be (0.0 +-(eps))
    Angle(0).unitY should be (1.0 +-(eps))

    Angle(PI/2).unitX should be (1.0 +-(eps))
    Angle(PI/2).unitY should be (0.0 +-(eps))

    Angle(PI).unitX should be ( 0.0 +-(eps))
    Angle(PI).unitY should be (-1.0 +-(eps))

    Angle(3*PI/2).unitX should be (-1.0 +-(eps))
    Angle(3*PI/2).unitY should be (0.0 +-(eps))
  }
}

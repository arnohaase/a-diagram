package com.ajjpj.adiagram.geometry

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite


/**
 * @author arno
 */
class APointTest extends FunSuite with ShouldMatchers {
  import Math._
  val eps = .000000001

  test("APoint create") {
    APoint(100, 200).x should be (100.0 plusOrMinus eps)
    APoint(100, 200).y should be (200.0 plusOrMinus eps)
  }

  test("APoint translate") {
    val p = APoint(100, 200) + (Angle(PI/6), 10)
    p.x should be (105.0 plusOrMinus(eps))
    p.y should be (200.0 + 10*cos(PI/6) plusOrMinus(eps))
  }

  test("APoint halfWayTo") {
    val p = APoint(1, 2) halfWayTo APoint(3, 4)
    p.x should be (2.0 plusOrMinus(eps))
    p.y should be (3.0 plusOrMinus(eps))
  }
}

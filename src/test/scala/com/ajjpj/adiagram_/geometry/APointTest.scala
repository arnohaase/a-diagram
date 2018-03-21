package com.ajjpj.adiagram_.geometry

import org.scalatest.words.ShouldVerb
import org.scalatest.{FlatSpec, FunSuite, Matchers}


/**
 * @author arno
 */
class APointTest extends FlatSpec with Matchers {
  import Math._
  val eps = .000000001

  "APoint" should "create a point" in {
    APoint(100, 200).x should be (100.0 +- eps)
    APoint(100, 200).y should be (200.0 +- eps)
  }

  it should "move by an offset given in angular coordinates" in {
    val p = APoint(100, 200) + (Angle(PI/6), 10)
    p.x should be (105.0 +- eps)
    p.y should be (200.0 + 10*cos(PI/6) +- eps)
  }

  it should "calculate the middle between two points" in {
    val p = APoint(1, 2) halfWayTo APoint(3, 4)
    p.x should be (2.0 +- eps)
    p.y should be (3.0 +- eps)
  }
}

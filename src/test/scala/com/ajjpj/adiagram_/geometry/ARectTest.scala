package com.ajjpj.adiagram_.geometry

import org.scalatest.{FunSuite, Matchers}

/**
 * @author arno
 */
class ARectTest extends FunSuite with Matchers {
  val eps = .000000001

  test("ARect: containing rect") {
    val r = ARect.containingRect(List(ARect((1.0, 5.0), 3, 4), ARect((5.0, 1.0), 3, 4)))
    r.topLeft.x should be (1.0 +- eps)
    r.topLeft.y should be (1.0 +- eps)
    r.bottomRight.x should be (8.0 +- eps)
    r.bottomRight.y should be (9.0 +- eps)
  }

  test("ARect contains") {
    val r = ARect (APoint(10, 20), 30, 40)

    r contains (15.0, 25.0) should equal (true)

    r contains (10.0, 20.0) should equal (true)
    r contains (10.0, 60.0) should equal (true)
    r contains (40.0, 20.0) should equal (true)
    r contains (40.0, 60.0) should equal (true)

    r contains (  0.0,   0.0) should equal (false)
    r contains (100.0, 100.0) should equal (false)

    r contains ( 9.9, 20.0) should equal (false)
    r contains (10.0, 19.9) should equal (false)
    r contains ( 9.9, 60.0) should equal (false)
    r contains (10.0, 60.1) should equal (false)
    r contains (40.1, 20.0) should equal (false)
    r contains (40.0, 19.9) should equal (false)
    r contains (40.1, 60.0) should equal (false)
    r contains (40.0, 60.1) should equal (false)
  }

  test("ARect intersection") {
    val r = ARect(APoint(1.0, 1.0), ADim(2.0, 2.0))

    r.intersection(APoint(2.0, 2.0), APoint(2.0, 4.0)).x should be (2.0 +- eps)
    r.intersection(APoint(2.0, 2.0), APoint(2.0, 4.0)).y should be (3.0 +- eps)
                                                                                    
    r.intersection(APoint(2.0, 2.0), APoint(2.0, 0.0)).x should be (2.0 +- eps)
    r.intersection(APoint(2.0, 2.0), APoint(2.0, 0.0)).y should be (1.0 +- eps)
                                                                                    
    r.intersection(APoint(2.0, 2.0), APoint(0.0, 2.0)).x should be (1.0 +- eps)
    r.intersection(APoint(2.0, 2.0), APoint(0.0, 2.0)).y should be (2.0 +- eps)

    r.intersection(APoint(2.0, 2.0), APoint(4.0, 2.0)).x should be (3.0 +- eps)
    r.intersection(APoint(2.0, 2.0), APoint(4.0, 2.0)).y should be (2.0 +- eps)
  }
}

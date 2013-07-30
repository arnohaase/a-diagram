package com.ajjpj.adiagram.geometry.transform

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import com.ajjpj.adiagram.geometry.{Angle, APoint}
import java.util.Random


/**
 * @author arno
 */
class AffineTransformationTest extends FunSuite with ShouldMatchers {
  val eps = .0000001
  import Math._

  test("affine translation") {
    val p = AffineTransformation.translation((2.0, 3.0)) ((4.0, 5.0))

    p.x should be (6.0 plusOrMinus(eps))
    p.y should be (8.0 plusOrMinus(eps))
  }

  test("affine rotation") {
    val orig = APoint(2, 1)

    val p0 = AffineTransformation.rotation((2.0, 0.0), Angle(PI / 4)) (orig)
    p0.x should be (2 + sqrt(2)/2 plusOrMinus eps)
    p0.y should be (    sqrt(2)/2 plusOrMinus eps)

    val p1 = AffineTransformation.rotation((0.0, 1.0), Angle(-PI/2)) (orig)
    p1.x should be (0.0 plusOrMinus eps);
    p1.y should be (3.0 plusOrMinus eps)
  }

  test("affine scaling") {
    val orig = APoint(2, 1)

    val p0 = AffineTransformation.scaling(APoint.ZERO, .5)(orig)
    p0.x should be (1.0 plusOrMinus eps)
    p0.y should be ( .5 plusOrMinus eps)

    val p1 = AffineTransformation.scaling((2.0, 0.0), 2)(orig)
    p1.x should be (2.0 plusOrMinus eps)
    p1.y should be (2.0 plusOrMinus eps)

    val p2 = AffineTransformation.scaling((0.0, 1.0), 3)(orig)
    p2.x should be (6.0 plusOrMinus eps)
    p2.y should be (1.0 plusOrMinus eps)
  }

  test("affine before after") {
    val t1 = AffineTransformation (Matrix.UNIT, (2.0, 3.0))
    val t2 = AffineTransformation (Matrix.scale(2), APoint.ZERO)
    val p = APoint(1, 0)

    (t1 after t2)(p).x should be (4.0 plusOrMinus eps)
    (t1 after t2)(p).y should be (3.0 plusOrMinus eps)

    (t1 before t2)(p).x should be (6.0 plusOrMinus eps)
    (t1 before t2)(p).y should be (6.0 plusOrMinus eps)
  }

  test("affine inverse") {
    val rand = new Random(2)

    for(i <- 0 to 1*1000) {
      val t = AffineTransformation (Matrix(rand.nextDouble()+2, rand.nextDouble(), rand.nextDouble(), rand.nextDouble()+2), ((rand.nextDouble() - .5)*100, (rand.nextDouble() - .5)*100))
      val p = APoint((rand.nextDouble()-.5)*100, (rand.nextDouble()-.5)*100)

      val p2 = t.inverse(t(p))

      p2.x should be (p.x plusOrMinus eps)
      p2.y should be (p.y plusOrMinus eps)
    }
  }
}



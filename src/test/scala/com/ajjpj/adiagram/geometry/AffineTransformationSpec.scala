package com.ajjpj.adiagram.geometry

import java.util.Random

import com.ajjpj.adiagram.ADiagramSpec


class AffineTransformationSpec extends ADiagramSpec {
  import Math._

  import LenUnit._

  "An AffineTransformation" should "perform a translation" in {
    val p = AffineTransformation.translation(Vector2(144, 216, point)) (Vector2(4, 5, inch))
    p.x shouldBe (6.0 +- eps)
    p.y shouldBe (8.0 +- eps)
    p.unit shouldBe inch
  }

  it should "perform a rotation" in {
    val orig = Vector2(2, 1, inch)

    val p0 = AffineTransformation.rotation(Vector2(144, 0, point), Angle(PI / 4)) (orig)
    p0.x shouldBe (2 + sqrt(2)/2 +- eps)
    p0.y shouldBe (    sqrt(2)/2 +- eps)
    p0.unit shouldBe inch

    val p1 = AffineTransformation.rotation(Vector2(0, 72, point), Angle(-PI/2)) (orig)
    p1.x shouldBe (0.0 +- eps)
    p1.y shouldBe (3.0 +- eps)
    p1.unit shouldBe inch
  }

  it should "scale relative to a given origin" in {
    val orig = Vector2(2, 1, inch)

    val p0 = AffineTransformation.scaling(Vector2.ZERO inUnit point, .5)(orig)
    p0.x should be (1.0 +- eps)
    p0.y should be ( .5 +- eps)
    p0.unit shouldBe inch

    val p1 = AffineTransformation.scaling(Vector2(144, 0, point), 2)(orig)
    p1.x shouldBe (2.0 +- eps)
    p1.y shouldBe (2.0 +- eps)
    p1.unit shouldBe inch

    val p2 = AffineTransformation.scaling(Vector2(0, 72, point), 3)(orig)
    p2.x shouldBe (6.0 +- eps)
    p2.y shouldBe (1.0 +- eps)
    p2.unit shouldBe inch
  }

  it should "compose affine transformations with 'before' and 'after'" in {
    val t1 = AffineTransformation (Matrix2.UNIT, Vector2(2, 3, inch))
    val t2 = AffineTransformation (Matrix2.scale(2), Vector2.ZERO)
    val p = Vector2(1, 0, inch)

    (t1 after t2)(p).x should be (4.0 +- eps)
    (t1 after t2)(p).y should be (3.0 +- eps)

    (t1 before t2)(p).x should be (6.0 +- eps)
    (t1 before t2)(p).y should be (6.0 +- eps)
  }

  it should "calculate its inverse" in {
    val rand = new Random(2)

    for(i <- 0 to 1*1000) {
      val t = AffineTransformation (Matrix2(rand.nextDouble()+2, rand.nextDouble(), rand.nextDouble(), rand.nextDouble()+2), Vector2((rand.nextDouble() - .5)*100, (rand.nextDouble() - .5)*100, mm))
      val p = Vector2((rand.nextDouble()-.5)*100, (rand.nextDouble()-.5)*100, mm)

      val p2 = t.inverse(t(p))

      p2.x shouldBe (p.x +- eps)
      p2.y shouldBe (p.y +- eps)
    }
  }
}

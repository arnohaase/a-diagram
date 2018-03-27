package com.ajjpj.adiagram.geometry

import com.ajjpj.adiagram.ADiagramSpec


class RectShapeSpec extends ADiagramSpec {
  import LenUnit._

  val r = RectShape (Vector2 (10, 20, inch), Vector2 (40*72, 60*72, pt))

  "A RectShape" should "convert its lower right corner to its common LenUnit" in {
    r.bottomRight.x shouldBe (40.0 +- eps)
    r.bottomRight.y shouldBe (60.0 +- eps)
    r.bottomRight.unit shouldBe inch
    r.unit shouldBe inch
  }

  it should "contain a point inside it" in {
    r contains Vector2(15, 25, inch) shouldBe true
  }

  it should "contain its corners" in {
    r contains Vector2 (10, 20, inch) shouldBe true
    r contains Vector2 (10, 60, inch) shouldBe true
    r contains Vector2 (40, 20, inch) shouldBe true
    r contains Vector2 (40, 60, inch) shouldBe true
  }

  it should "not contain points way outside it" in {
    r contains Vector2 (0.0, 0.0, inch) shouldBe false
    r contains Vector2 (100, 100, inch) shouldBe false
  }

  it should "not contain points close to its sides but outside it" in {
    r contains Vector2( 9.9, 20.0, inch) shouldBe false
    r contains Vector2(10.0, 19.9, inch) shouldBe false
    r contains Vector2( 9.9, 60.0, inch) shouldBe false
    r contains Vector2(10.0, 60.1, inch) shouldBe false
    r contains Vector2(40.1, 20.0, inch) shouldBe false
    r contains Vector2(40.0, 19.9, inch) shouldBe false
    r contains Vector2(40.1, 60.0, inch) shouldBe false
    r contains Vector2(40.0, 60.1, inch) shouldBe false
  }

  "RectShape.fromDim" should "create a rectangle based on width and height" in {
    val r2 = RectShape.fromDim(Vector2(10, 20, inch), Vector2(30*72, 40*72, pt))

    r2.topLeft.x shouldBe (10.0 +- eps)
    r2.topLeft.y shouldBe (20.0 +- eps)
    r2.topLeft.unit shouldBe inch

    r2.bottomRight.x shouldBe (40.0 +- eps)
    r2.bottomRight.y shouldBe (60.0 +- eps)
    r2.bottomRight.unit shouldBe inch

    r2.unit shouldBe inch
  }

  "RectShape.intersection" should "calculate the point where a line intersects its boundaries" in {
    val r = RectShape(Vector2(1, 1, inch), Vector2(3, 3, inch))

    val bottom = r.intersection(Vector2(2, 2, inch), Vector2(2, 4, inch))
    bottom.x shouldBe (2.0 +- eps)
    bottom.y shouldBe (3.0 +- eps)

    val top = r.intersection(Vector2(2, 2, inch), Vector2(2, 0, inch))
    top.x shouldBe (2.0 +- eps)
    top.y shouldBe (1.0 +- eps)

    val left = r.intersection(Vector2(2, 2, inch), Vector2(0, 2, inch))
    left.x shouldBe (1.0 +- eps)
    left.y shouldBe (2.0 +- eps)

    val right = r.intersection(Vector2(2, 2, inch), Vector2(4, 2, inch))
    right.x shouldBe (3.0 +- eps)
    right.y shouldBe (2.0 +- eps)
  }

  "RectShape.createWithPadding" should "add uniform padding around a RectShape" in {
    val r = RectShape.createWithPadding(Vector2(1, 2, inch), Vector2(3*72, 4*72, pt), Length(2.54, mm))
    r.left.l shouldBe (.9 +- eps)
    r.right.l shouldBe (3.1 +- eps)
    r.top.l shouldBe (1.9 +- eps)
    r.bottom.l shouldBe (4.1 +- eps)

    r.left.unit shouldBe inch
    r.right.unit shouldBe inch
    r.top.unit shouldBe inch
    r.bottom.unit shouldBe inch
  }
}

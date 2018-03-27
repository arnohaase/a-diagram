package com.ajjpj.adiagram.geometry

import com.ajjpj.adiagram.ADiagramSpec


class AngleSpec extends ADiagramSpec {
  import Math._

  "An Angle" should "contain its normalized value" in {
    Angle(1).rad shouldBe (1.0 +- eps)
    Angle(0).rad shouldBe (0.0 +- eps)
    Angle(PI).rad shouldBe (PI +- eps)
    Angle(6).rad shouldBe (6.0 +- eps)

    Angle(2*PI).rad shouldBe (0.0 +- eps)
    Angle(3*PI).rad shouldBe (PI +- eps)
    Angle(-1).rad shouldBe (2*PI - 1 +- eps)
    Angle(-2*PI).rad shouldBe (0.0 +- eps)
  }

  it should "calculate its screenDegrees " in {
    Angle(0).screenDegrees shouldBe (270.0 +- eps)
    Angle(PI/4).screenDegrees shouldBe (315.0 +- eps)
    Angle(PI/2).screenDegrees shouldBe (0.0 +- eps)
    Angle(PI).screenDegrees shouldBe (90.0 +- eps)
    Angle(3*PI/2).screenDegrees shouldBe (180.0 +- eps)
  }

  it should "calculate unitX and unitY" in {
    Angle(0).unitX shouldBe (0.0 +- eps)
    Angle(PI/2).unitX shouldBe (1.0 +- eps)
    Angle(PI).unitX shouldBe (0.0 +- eps)
    Angle(3*PI/2).unitX shouldBe (-1.0 +- eps)

    Angle(0).unitY shouldBe (1.0 +- eps)
    Angle(PI/2).unitY shouldBe (0.0 +- eps)
    Angle(PI).unitY shouldBe (-1.0 +- eps)
    Angle(3*PI/2).unitY shouldBe (0.0 +- eps)
  }

  it should "rotate and calculate its opposite" in {
    Angle(0).cw90.rad shouldBe (3*PI/2 +- eps)
    Angle(PI/2).cw90.rad shouldBe (0.0 +- eps)
    Angle(PI).cw90.rad shouldBe (PI/2 +- eps)
    Angle(3*PI/2).cw90.rad shouldBe (PI +- eps)

    Angle(0).ccw90.rad shouldBe (PI/2 +- eps)
    Angle(PI/2).ccw90.rad shouldBe (PI +- eps)
    Angle(PI).ccw90.rad shouldBe (3*PI/2 +- eps)
    Angle(3*PI/2).ccw90.rad shouldBe (0.0 +- eps)

    Angle(0).opposite.rad shouldBe (PI +- eps)
    Angle(PI/2).opposite.rad shouldBe (3*PI/2 +- eps)
    Angle(PI).opposite.rad shouldBe (0.0 +- eps)
    Angle(3*PI/2).opposite.rad shouldBe (PI/2 +- eps)
  }

  it should "add and subtract another Angle" in {
    (Angle(1) + Angle(2)).rad shouldBe(3.0 +- eps)
    (Angle(4) + Angle(5)).rad shouldBe(9 - 2*PI +- eps)

    (Angle(3) - Angle(2)).rad shouldBe(1.0 +- eps)
    (Angle(2) - Angle(3)).rad shouldBe(2*PI - 1 +- eps)
  }

  it should "be creatable from dx and dy" in {
    Angle.fromDxDy(0, 1).rad shouldBe (0.0 +- eps)
    Angle.fromDxDy(-1, 0).rad shouldBe (3*PI/2 +- eps)
    Angle.fromDxDy(0, -1).rad shouldBe (PI +- eps)
    Angle.fromDxDy(1, 0).rad shouldBe (PI/2 +- eps)

    Angle.fromDxDy(1, 1).rad shouldBe (PI/4 +- eps)
  }

  it should "be creatable from two points" in {
    import LenUnit._
    Angle.fromLine(Vector2(1, 1 ,mm), Vector2(1, 2, mm)).rad shouldBe (0.0 +- eps)
    Angle.fromLine(Vector2(1, 1 ,mm), Vector2(0, 1, mm)).rad shouldBe (3*PI/2 +- eps)
    Angle.fromLine(Vector2(1, 1 ,mm), Vector2(1, 0, mm)).rad shouldBe (PI +- eps)
    Angle.fromLine(Vector2(1, 1 ,mm), Vector2(2, 1, mm)).rad shouldBe (PI/2 +- eps)

    Angle.fromLine(Vector2(1, 1 ,mm), Vector2(2, 2, mm)).rad shouldBe (PI/4 +- eps)
  }
}

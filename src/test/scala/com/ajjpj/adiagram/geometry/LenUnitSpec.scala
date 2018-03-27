package com.ajjpj.adiagram.geometry

import com.ajjpj.adiagram.ADiagramSpec
import org.scalatest.{FlatSpec, Matchers}


class LenUnitSpec extends ADiagramSpec {
  "A LenUnit" should "convert to and from 'point'" in {
    val lu12 = LenUnit(12)
    lu12.factorToPoint shouldBe (12.0 +- eps)
    lu12.factorFromPoint shouldBe (1 / 12.0 +- eps)

    val lu6 = LenUnit(6)
    lu12.convertTo(2, lu6) shouldBe (4.0 +- eps)
    lu6.convertTo(2.0, lu12) shouldBe (1.0 +- eps)
  }

  "LenUnit.point" should "convert correctly" in {
    LenUnit.pt.convertTo(5, LenUnit.pt) shouldBe (5.0 +- eps)
  }

  "LenUnit.mm" should "convert correctly" in {
    LenUnit.mm.convertTo(25.4, LenUnit.pt) shouldBe (72.0 +- eps)
  }

  "LenUnit.inch" should "convert correctly" in {
    LenUnit.inch.convertTo(2, LenUnit.pt) shouldBe (144.0 +- eps)
  }
}

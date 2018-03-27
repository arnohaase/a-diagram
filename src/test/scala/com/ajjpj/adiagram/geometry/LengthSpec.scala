package com.ajjpj.adiagram.geometry

import com.ajjpj.adiagram.ADiagramSpec


class LengthSpec extends ADiagramSpec {
  "A Length" should "contain its length and unit" in {
    Length(1, LenUnit.pt).l shouldBe (1.0 +- eps)
    Length(1, LenUnit.pt).unit shouldBe LenUnit.pt
  }

  it should "convert itself to a different LenUnit" in {
    val conv = Length(1, LenUnit.inch).inUnit(LenUnit.pt)
    conv.l shouldBe(72.0 +- eps)
    conv.unit shouldBe LenUnit.pt
  }
}

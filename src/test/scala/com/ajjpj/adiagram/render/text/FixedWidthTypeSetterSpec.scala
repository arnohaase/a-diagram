package com.ajjpj.adiagram.render.text

import com.ajjpj.adiagram.ADiagramSpec


class FixedWidthTypeSetterSpec extends ADiagramSpec {
  "FixedWidthTypeSetter.splitIntoWords" should "split a sentence into words" in {
    FixedWidthTypeSetter.splitToWords("The dog jumps") shouldBe Vector("The", " ", "dog", " ", "jumps")
  }

  it should "separate duplicate blanks" in {
    FixedWidthTypeSetter.splitToWords("a  b") shouldBe Vector("a", " ", " ", "b")
  }

  it should "keep leadinog and trailing blanks" in {
    FixedWidthTypeSetter.splitToWords("  a  ") shouldBe Vector(" ", " ", "a", " ", " ")
  }

  it should "handle the empty string" in {
    FixedWidthTypeSetter.splitToWords("") shouldBe Vector()
  }
}

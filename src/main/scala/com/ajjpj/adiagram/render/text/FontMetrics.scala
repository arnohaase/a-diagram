package com.ajjpj.adiagram.render.text

import javafx.scene.text.{Font, Text}


class FontMetrics(font: Font) {
  private val internal = new Text
  internal.setFont(font)
  private val b = internal.getLayoutBounds

  val lineHeight = b.getHeight
  val ascent = -b.getMinY
  val descent = b.getMaxY

  def computeStringWidth(txt: String) = {
    internal.setText(txt)
    internal.getLayoutBounds.getWidth
  }
}

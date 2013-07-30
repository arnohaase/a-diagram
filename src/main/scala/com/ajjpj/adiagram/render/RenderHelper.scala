package com.ajjpj.adiagram.render

import javafx.scene.SnapshotParameters
import javafx.scene.paint.Color
import javafx.scene.canvas.Canvas
import javafx.scene.text.Font
import com.sun.javafx.tk.Toolkit
import com.ajjpj.adiagram.ui.fw.{JavaFxHelper, Digest}

/**
 * @author arno
 */
object RenderHelper {
  val TransparentSnapshotParameters = {
    val result = new SnapshotParameters()
    result.setFill(Color.TRANSPARENT)
    result
  }

  def snapshot(canvas: Canvas)(implicit digest: Digest) = JavaFxHelper.inUiThreadAndWait(canvas.snapshot(TransparentSnapshotParameters, null)).get // it is ok for None to trigger an exception - it was caused by one

  def actualHeightInPixels(f: Font) = {
    val fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(f);
    fontMetrics.getLineHeight();
  }


  def font(sizeInPixels: Double): Font = {
    val raw = new Font(sizeInPixels)
    val scaleFactor = actualHeightInPixels(raw) / sizeInPixels
    new Font(sizeInPixels / scaleFactor)
  }
}

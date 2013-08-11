package com.ajjpj.adiagram.render

import javafx.scene.{Node, SnapshotParameters}
import javafx.scene.paint.Color
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

  private def snapshotParams(bgColor: Color) = {
    val result = new SnapshotParameters
    result.setFill(bgColor)
    result
  }

  //TODO replace 'new Digest' with something else that does the correct exception handling but not the event loop
  def snapshot(node: Node, bgColor: Color = Color.TRANSPARENT) = JavaFxHelper.inUiThreadAndWait(node.snapshot(snapshotParams(bgColor), null))(new Digest()).get // it is ok for None to trigger an exception - it was caused by one

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

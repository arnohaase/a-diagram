package com.ajjpj.adiagram.render

import javafx.application.Platform
import javafx.scene.image.WritableImage
import javafx.scene.paint.{Color, Paint}
import javafx.scene.text.Font
import javafx.scene.{Node, SnapshotParameters}

import com.sun.javafx.tk.Toolkit

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}


object RenderHelper {
  def snapshot(node: Node, bgColor: Paint = Color.TRANSPARENT): WritableImage = {
    def snapshotParameters = {
      val p = new SnapshotParameters
      p.setFill(bgColor)
      p
    }
    def doIt() = node.snapshot(snapshotParameters, null)
    
    if(Platform.isFxApplicationThread) {
      doIt()
    }
    else {
      val promise = Promise[WritableImage]()
      Platform.runLater(() => {
        promise.success(doIt())
      })
      Await.result (promise.future, 10.seconds)
    }
  }

  def actualHeightInPixels(f: Font) = {
    val fontMetrics = Toolkit.getToolkit.getFontLoader.getFontMetrics(f)
    fontMetrics.getLineHeight
  }

  def font(sizeInPixels: Double): Font = {
    val raw = new Font(sizeInPixels)
    val scaleFactor = actualHeightInPixels(raw) / sizeInPixels
    new Font(sizeInPixels / scaleFactor)
  }
}

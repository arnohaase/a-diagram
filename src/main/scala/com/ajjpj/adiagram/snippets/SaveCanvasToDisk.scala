package com.ajjpj.adiagram.snippets

import javafx.stage.Stage
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.effect.DropShadow
import javax.imageio.ImageIO
import javafx.embed.swing.SwingFXUtils
import java.io.File


/**
 * @author arno
 */
object SaveCanvasToDisk {
  def main(args: Array[String]) {
    javafx.application.Application.launch(classOf[SaveCanvasToDisk])
  }
}

class SaveCanvasToDisk extends javafx.application.Application {
  def start(primaryStage: Stage) {
    saveCanvas()
  }

  private def saveCanvas() {
    val c = new Canvas(500, 500)
    val gc = c.getGraphicsContext2D
    gc.setFill(Color.BLUE)
    gc.fillRect(20, 30, 40, 50)
    gc.applyEffect(new DropShadow)
    val sp = new SnapshotParameters
    sp.setFill(Color.TRANSPARENT)
    val img = c.snapshot(sp, null)
    ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", new File("/home/arno/dummyrect.png"))
    System.exit(0)
  }
}

package com.ajjpj.adiagram.model

import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.effect.DropShadow
import javafx.scene.SnapshotParameters
import javax.imageio.ImageIO
import javafx.embed.swing.SwingFXUtils
import java.io.File
import com.ajjpj.adiagram.ui.Zoom
import com.ajjpj.adiagram.ui.presentation.{DiagramRootContainer, ADiagramController}
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import com.ajjpj.adiagram.model.diagram.ADiagram
import com.ajjpj.adiagram.ui.fw.Digest


/**
 * @author arno
 */
object DiagramExportToImage {
  def exportToImageFile(ctrl: ADiagramController) {
    val zoom = Zoom(4) //TODO select on-screen

    val fileChooser = new FileChooser
    fileChooser.setTitle("Export Diagram to Image File")
    fileChooser.getExtensionFilters.add(new ExtensionFilter("*.png", "*.png"))
    //TODO initial directory; initial file name

    val fileRaw = fileChooser.showSaveDialog(ctrl.window)
    if(fileRaw != null) {
      val file = if (fileRaw.getName endsWith ".png") fileRaw else new File(fileRaw.getParent, fileRaw.getName + ".png")

      val img = createImage(ctrl.diagram, zoom)
    }
  }

  private def createImage(diagram: ADiagram, zoom: Zoom) = {
    val partialImages = diagram.elements.map(_.shape.render(zoom))

    implicit val digest = new Digest()
    val pane = new DiagramRootContainer()

    //TODO extract render code from ADiagramController

    digest.execute {
//      partialImages.flatMap(_.shadow).foreach(x => pane.add (x.))

    }


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

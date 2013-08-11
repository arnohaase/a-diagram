package com.ajjpj.adiagram.model

import javafx.scene.canvas.Canvas
import javafx.scene.Group
import javax.imageio.ImageIO
import javafx.embed.swing.SwingFXUtils
import java.io.File
import com.ajjpj.adiagram.ui.Zoom
import com.ajjpj.adiagram.ui.presentation.{ByZComparator, CanvasWithDerivedZOrder, ShapePresentationHelper, ADiagramController}
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import com.ajjpj.adiagram.model.diagram.ADiagram
import javafx.collections.FXCollections
import com.ajjpj.adiagram.render.RenderHelper


/**
 * @author arno
 */
object DiagramExportToImage {
  def exportToImageFile(ctrl: ADiagramController) {
    val zoom = Zoom(4) //TODO select on-screen
    //TODO configurable: background transparent or white

    val fileChooser = new FileChooser
    fileChooser.setTitle("Export Diagram to Image File")
    fileChooser.getExtensionFilters.add(new ExtensionFilter("*.png", "*.png"))
    //TODO initial directory; initial file name

    val fileRaw = fileChooser.showSaveDialog(ctrl.window)
    if(fileRaw != null) {
      val file = if (fileRaw.getName endsWith ".png") fileRaw else new File(fileRaw.getParent, fileRaw.getName + ".png")
      val img = createImage(ctrl.diagram, zoom)

      ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file)
    }
  }

  private def createImage(diagram: ADiagram, zoom: Zoom) = {
    val pane = new Group

    //TODO render multithreaded?
    diagram.elements.foreach(spec => {
      val pi = spec.shape.render(zoom)

      val shapeCanvas = new CanvasWithDerivedZOrder(spec)
      val shadowCanvas = new Canvas
      pane.getChildren.addAll(shapeCanvas, shadowCanvas)
      ShapePresentationHelper.drawShapeOnCanvas(pi, spec.pos, shapeCanvas, shadowCanvas, zoom)
    })

    FXCollections.sort(pane.getChildren, ByZComparator)

    RenderHelper.snapshot(pane)
  }
}

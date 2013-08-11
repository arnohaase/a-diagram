package com.ajjpj.adiagram.model

import javafx.scene.canvas.Canvas
import javax.imageio.ImageIO
import javafx.embed.swing.SwingFXUtils
import java.io.File
import com.ajjpj.adiagram.ui.{AScreenRect, Zoom}
import com.ajjpj.adiagram.ui.presentation.{ByZComparator, CanvasWithDerivedZOrder, ShapePresentationHelper, ADiagramController}
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import com.ajjpj.adiagram.model.diagram.ADiagram
import javafx.collections.FXCollections
import com.ajjpj.adiagram.render.RenderHelper
import javafx.scene.layout.Pane
import com.ajjpj.adiagram.geometry.{APoint, ARect}


/**
 * @author arno
 */
object DiagramExportToImage {
  def exportToImageFile(ctrl: ADiagramController) {
    val zoom = Zoom(4) //TODO select on-screen
    //TODO configurable: background transparent or white
    //TODO configurable: (black) frame around the image?

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

  private def diagramRenderBounds(diagram: ADiagram) = ARect.containingRect(diagram.elements.map(_.shape.renderBounds))
  private def resultingPixelSize(diagram: ADiagram, zoom: Zoom) = AScreenRect(diagramRenderBounds(diagram), zoom) withPadding 1

  private def createImage(diagram: ADiagram, zoom: Zoom) = {
    val pane = new Pane

    val bounds = diagramRenderBounds(diagram)
    println(bounds)
    println(resultingPixelSize(diagram, zoom).width + " / " + resultingPixelSize(diagram, zoom).height)
    val topLeft = bounds.topLeft + APoint(2.0, 2.0) // was added internally for 'bleeding' - overshooting is harmless here, btw.

    //TODO render multithreaded?
    diagram.elements.foreach(spec => {
      val pi = spec.shape.render(zoom)

      val shapeCanvas = new CanvasWithDerivedZOrder(spec)
      val shadowCanvas = new Canvas
      pane.getChildren.addAll(shapeCanvas, shadowCanvas)
      ShapePresentationHelper.drawShapeOnCanvas(pi, spec.pos - topLeft, shapeCanvas, shadowCanvas, zoom)
    })

    FXCollections.sort(pane.getChildren, ByZComparator)

    RenderHelper.snapshot(pane)
  }
}

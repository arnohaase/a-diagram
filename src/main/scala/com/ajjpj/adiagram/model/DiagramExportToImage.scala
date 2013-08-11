package com.ajjpj.adiagram.model

import javafx.scene.canvas.Canvas
import javax.imageio.ImageIO
import javafx.embed.swing.SwingFXUtils
import java.io.File
import com.ajjpj.adiagram.ui.{AScreenRect, Zoom}
import com.ajjpj.adiagram.ui.presentation.{ByZComparator, CanvasWithDerivedZOrder, ShapePresentationHelper, ADiagramController}
import com.ajjpj.adiagram.model.diagram.ADiagram
import javafx.collections.FXCollections
import com.ajjpj.adiagram.render.RenderHelper
import javafx.scene.layout.Pane
import com.ajjpj.adiagram.geometry.{APoint, ARect}
import com.ajjpj.adiagram.ui.forms.AbstractForm
import javafx.scene.control.{TextField, Label}
import com.ajjpj.adiagram.ui.fw.{Unbindable, Digest}
import com.ajjpj.adiagram.ui.fw.JavaFxHelper.{ButtonSpec, ButtonPane, Dialog}
import javafx.scene.paint.Color
import javafx.scene.shape.{StrokeType, Rectangle}


/**
 * @author arno
 */
object DiagramExportToImage {
  def exportToImageFile(ctrl: ADiagramController)(implicit digest: Digest) {
    //TODO configurable: background transparentBackground or white
    //TODO configurable: (black) frame around the image? --> what width to use?

    new ExportDialog(ctrl).show()
  }

  private def doExport(diagram: ADiagram, zoom: Zoom, file: File, bgColor: Color, withBorder: Boolean) {
    //TODO check if file exists --> 'overwrite' dialog
    val img = createImage(diagram, zoom, bgColor, withBorder)
    ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file)
  }

  //TODO make 'Export' the default button --> activate with 'enter'

  private class ExportDialog(ctrl: ADiagramController)(implicit digest: Digest) extends Dialog(ctrl.window, "Export to Image File") {
    override lazy val content = new ExportForm(ctrl) // must be lazy because it is called from the super class' constructor
    override def buttonPane = new ButtonPane(List(ButtonSpec("Export", "export", content.file.isDefined && content.isPngFile), ButtonSpec.cancel()), onClicked = onClicked)

    private def onClicked(btnId: String) = btnId match {
      case "export" =>
        doExport(ctrl.diagram, content.zoom, content.file.map(new File(_)).get, Color.TRANSPARENT, true)
        hide()
      case ButtonSpec.idCancel =>
        hide()
    }
  }

  private class ExportForm(ctrl: ADiagramController)(implicit digest: Digest) extends AbstractForm with Unbindable {
    private def asPngFileName(f: File) = {
      val name = f.getName
      val withoutExtension = name.lastIndexOf('.') match {
        case -1 => name
        case idx => name.substring(0, idx)
      }

      new File(f.getParent, withoutExtension + ".png").getAbsolutePath
    }

    var file: Option[String] = ctrl.file.map(asPngFileName)
    def fileExists = file.exists(new File(_).exists)
    def isPngFile  = file.map(new File(_)).exists(f => f.getName.endsWith(".png") && ! f.isDirectory)

    def zoom = new Zoom(2) //TODO

    val txtFile = new TextField //TODO initially scroll horizontally to the *end* of the content (or mark the name without the extension)

    add(new Label("File Name:"), 0, 0)
    add(txtFile, 1, 0)

    bind(txtFile.textProperty, file.getOrElse(""), (f: String) => {
      file = if(f.trim.isEmpty) None else Some(f)
    })

    override def unbind()(implicit digest: Digest) {
      digest.unbind(txtFile.textProperty)
    }
  }

  private def diagramRenderBounds(diagram: ADiagram) = ARect.containingRect(diagram.elements.map(_.shape.renderBounds))
  private def resultingPixelSize(diagram: ADiagram, zoom: Zoom) = AScreenRect(diagramRenderBounds(diagram), zoom) withPadding 1

  private def createImage(diagram: ADiagram, zoom: Zoom, bgColor: Color, withBorder: Boolean) = {
    val pane = new Pane

    val bounds = diagramRenderBounds(diagram)
    println(bounds)
    println(resultingPixelSize(diagram, zoom).topLeft)
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

    if(withBorder) {
      val border = new Rectangle
      border.setWidth(resultingPixelSize(diagram, zoom).width)
      border.setHeight(resultingPixelSize(diagram, zoom).height)
      border.setStrokeWidth(2*zoom.factor)
      border.setStrokeType(StrokeType.INSIDE)
      border.setStroke(Color.BLACK)

      border.setX(-2*zoom.factor - 1)
      border.setY(-2*zoom.factor - 1)

      border.setFill(Color.TRANSPARENT)

      pane.getChildren.add(border)
    }

    FXCollections.sort(pane.getChildren, ByZComparator)

    RenderHelper.snapshot(pane, bgColor)
  }
}

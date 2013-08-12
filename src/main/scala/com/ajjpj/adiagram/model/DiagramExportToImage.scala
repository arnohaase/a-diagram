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
import javafx.scene.layout.{HBox, Pane}
import com.ajjpj.adiagram.geometry.{APoint, ARect}
import com.ajjpj.adiagram.ui.forms.AbstractForm
import javafx.scene.control._
import com.ajjpj.adiagram.ui.fw.{JavaFxHelper, SystemConfiguration, Unbindable, Digest}
import com.ajjpj.adiagram.ui.fw.JavaFxHelper.{ButtonSpec, ButtonPane, Dialog}
import javafx.scene.paint.Color
import javafx.scene.shape.{StrokeType, Rectangle}
import scala.Some
import com.ajjpj.adiagram.util._
import javafx.stage.Window

/**
 * @author arno
 */
object DiagramExportToImage {
  def exportToImageFile(ctrl: ADiagramController)(implicit digest: Digest) {
    new ExportDialog(ctrl).show()
  }

  private def doExport(diagram: ADiagram, zoom: Zoom, file: File, bgColor: Color, withBorder: Boolean, window: Window)(implicit digest: Digest) {
    if(JavaFxHelper.confirmOverwrite(file, window)) {
      val img = createImage(diagram, zoom, bgColor, withBorder)
      ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file)
    }
  }

  private class ExportDialog(ctrl: ADiagramController)(implicit digest: Digest) extends Dialog(ctrl.window, "Export to Image File") {
    override lazy val content = new ExportForm(ctrl) // must be lazy because it is called from the super class' constructor
    override def buttonPane = new ButtonPane(List(ButtonSpec("Export", "export", enabled=content.file.isDefined && content.isPngFile, default=true), ButtonSpec.cancel()), onClicked = onClicked)

    private def onClicked(btnId: String) = btnId match {
      case "export" =>
        doExport(ctrl.diagram, content.zoom, content.file.map(new File(_)).get, if(content.renderTransparentBackground) Color.TRANSPARENT else Color.WHITE, withBorder=content.renderBorder, ctrl.window)
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

    var renderTransparentBackground = SystemConfiguration.exportRenderTransparentBackground
    var renderBorder = SystemConfiguration.exportRenderBorder

    var zoom = new Zoom(SystemConfiguration.exportDefaultZoom)
    private def updateZoom(s: String): Unit = ignoringException {updateZoom(s.toDouble)}
    private def updateZoom(d: Double) { if(d >= SystemConfiguration.exportMinZoom && d <= SystemConfiguration.exportMaxZoom) zoom = Zoom(d) }


    val txtFile = new TextField //TODO initially scroll horizontally to the *end* of the content (or mark the name without the extension)
    bind(txtFile.textProperty, file.getOrElse(""), (f: String) => { file = if(f.trim.isEmpty) None else Some(f) })

    val hboxBg = new HBox(8)
    val radioTransparent = new RadioButton("Transparent")
    val radioWhite = new RadioButton("White")
    val toggleBg = new ToggleGroup
    toggleBg.getToggles.addAll(radioTransparent, radioWhite)
    hboxBg.getChildren.addAll(radioTransparent, radioWhite)
    bindBoolean(radioTransparent.selectedProperty,   renderTransparentBackground, (b: Boolean) => {renderTransparentBackground = b})
    bindBoolean(radioWhite.      selectedProperty, ! renderTransparentBackground, (b: Boolean) => {renderTransparentBackground = !b})

    val chkBorder = new CheckBox()
    bindBoolean(chkBorder.selectedProperty, renderBorder, (b: Boolean) => {renderBorder = b})

    val txtZoom = new TextField
    txtZoom.setMaxWidth(80)
    //TODO make slider logarithmic
    val sliderZoom = new Slider(SystemConfiguration.exportMinZoom, SystemConfiguration.exportMaxZoom, 1)
    val hboxZoom = new HBox(8)
    hboxZoom.getChildren.addAll(txtZoom, sliderZoom)
    //TODO handle '.' and ','
    //TODO formatting: show only two fraction digits
    bind (txtZoom.textProperty, zoom.factor.toString(), (s: String) => updateZoom(s) )
    bindDouble (sliderZoom.valueProperty, zoom.factor, (d: Double) => updateZoom(d) )

    val txtX = new TextField
    val txtY = new TextField
    val hboxXy = new HBox(8)
    hboxXy.getChildren.addAll(txtX, new Label("x"), txtY)
    txtX.setMaxWidth(80)
    txtY.setMaxWidth(80)
    //TODO display rounded values --> problem: rounding changes the zoom, and the binding loop goes on
    bind(txtX.textProperty, resultingPixelSize(ctrl.diagram, zoom).width.toString,  zoomFromWidth)
    bind(txtY.textProperty, resultingPixelSize(ctrl.diagram, zoom).height.toString, zoomFromHeight)
//    bind(txtX.textProperty, Math.ceil(resultingPixelSize(ctrl.diagram, zoom).width). toInt.toString, zoomFromWidth)
//    bind(txtY.textProperty, Math.ceil(resultingPixelSize(ctrl.diagram, zoom).height).toInt.toString, zoomFromHeight)
    def zoomFromWidth(s: String) = ignoringException {
      val newWidth=s.toDouble - 2
      val factor = newWidth / ( resultingPixelSize(ctrl.diagram, zoom).width -2)
      updateZoom(zoom.factor * factor)
    }
    def zoomFromHeight(s: String) = ignoringException {
      val newHeight=s.toDouble - 2
      val factor = newHeight / ( resultingPixelSize(ctrl.diagram, zoom).height -2)
      updateZoom(zoom.factor * factor)
    }

    add(new Label("File Name:"), 0, 0)
    add(txtFile, 1, 0)
    add(new Label("Background:"), 0, 1)
    add(hboxBg, 1, 1)
    add(new Label("Border:"), 0, 2)
    add(chkBorder, 1, 2)
    add(new Label("Zoom:"), 0, 3)
    add(hboxZoom, 1, 3)
    add(new Label("Image Size:"), 0, 4)
    add(hboxXy, 1, 4)
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

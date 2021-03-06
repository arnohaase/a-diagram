package com.ajjpj.adiagram_.ui

import com.ajjpj.adiagram_.ui.fw.{JavaFxHelper, Digest}
import javafx.scene.canvas.Canvas
import com.ajjpj.adiagram_.render.base.PartialImageWithShadow
import com.ajjpj.adiagram_.geometry.APoint
import com.ajjpj.adiagram_.ui.mouse.MouseTracker
import com.ajjpj.adiagram_.model.diagram.{ShapeSpecReRenderSnapshot, AShapeSpec, ADiagram}
import com.ajjpj.adiagram_.model.SelectedStyles
import com.ajjpj.adiagram_.model.style.AStyleRepository
import java.io.File
import javafx.stage.Stage
import com.ajjpj.adiagram_.ui.presentation.{ShapePresentationHelper, CanvasWithDerivedZOrder, DiagramRootContainer}


/**
 * @author arno
 */
class ADiagramController (val root: DiagramRootContainer, val diagram: ADiagram, val styleRepository: AStyleRepository, val selectedStyles: SelectedStyles, var file: Option[File])(implicit val digest: Digest) {
  var zoom = Zoom.Identity

  def isDirty = digest.undoRedo.hasUndo
  def isPristine = file.isEmpty && ! isDirty

  def windowTitle = "A-Diagram - " + (file match {
    case Some(f) => f.getName
    case None => "<New Diagram>"
  }) + (if (isDirty) "*" else "")

  def window = root.getScene.getWindow.asInstanceOf[Stage]

  val selections = new SelectionTracker(diagram, root, this)
  val mouseTracker = new MouseTracker(this)

  private var detailsByElement = Map[AShapeSpec, RenderDetails]()

  digest.registerPostprocessor(updatePresentation)

  private def updatePresentation() {
    def unregisterElement(spec: AShapeSpec) {
      root.getChildren.removeAll(detailsByElement(spec).shapeCanvas, detailsByElement(spec).shadowCanvas)
      detailsByElement -= spec
    }

    def registerElement(spec: AShapeSpec) {
      val details = RenderDetails(-1, new CanvasWithDerivedZOrder(spec), new Canvas(), APoint.ZERO, APoint.ZERO, null)
      root.getChildren.addAll(details.shapeCanvas, details.shadowCanvas)
      detailsByElement += (spec -> details)
    }


    def render(spec: AShapeSpec) = {
      val zoomSnapshot = zoom

      def render: PartialImageWithShadow = spec.shape.render(zoomSnapshot)

      val snapshotCurrentlyRendered = SnapshotWithZoom(zoomSnapshot, spec.snapshot)
      if(detailsByElement(spec).snapshot != snapshotCurrentlyRendered) {
        val counterSnapshot = spec.changeCounter

        JavaFxHelper.inBackgroundNoDigest(render, (pi: PartialImageWithShadow) => {
          detailsByElement.get(spec) match {
            //TODO change '<=' to '<' : Problem is repaint of bound lines
            case Some(details) if details.changeCounter <= counterSnapshot =>
              ShapePresentationHelper.drawShapeOnCanvas(pi, spec.pos, details.shapeCanvas, details.shadowCanvas, zoom)
              detailsByElement += (spec -> details.copy(
                snapshot = snapshotCurrentlyRendered,
                changeCounter = counterSnapshot,
                shapeOffset = pi.shape.renderOffset,
                shadowOffset = pi.shadow.map(_.renderOffset).getOrElse(APoint.ZERO)
              ))
            case None =>
          }
        }) // no changes are performed in this UI callback --> no need for events to be triggered
      }
    }

    def refreshPos(spec: AShapeSpec) = {
      val details = detailsByElement(spec)
      ShapePresentationHelper.refreshPos(details.shapeCanvas, details.shadowCanvas, spec.pos, details.shapeOffset, details.shadowOffset, zoom)
    }

    // deal with added and removed elements
    detailsByElement.keys.filterNot(diagram.elements.       contains).foreach(unregisterElement)
    diagram.elements.     filterNot(detailsByElement.keySet.contains).foreach(registerElement)

    // update changed elements
    //TODO work with snapshots, re-render only changed elements
    diagram.elements.foreach (render)
    diagram.elements.foreach (refreshPos)
  }

  private case class SnapshotWithZoom (zoom: Zoom, snapshot: ShapeSpecReRenderSnapshot)

  private case class RenderDetails(changeCounter: Int, shapeCanvas: Canvas, shadowCanvas: Canvas, shapeOffset: APoint, shadowOffset: APoint, snapshot: SnapshotWithZoom)
}

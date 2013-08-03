package com.ajjpj.adiagram.ui.presentation

import com.ajjpj.adiagram.model.{ShapeSpecReRenderSnapshot, AShapeSpec, ADiagram}
import com.ajjpj.adiagram.ui.fw.{JavaFxHelper, Digest}
import com.ajjpj.adiagram.ui.{MouseTracker, SelectionTracker}
import javafx.scene.canvas.Canvas
import com.ajjpj.adiagram.render.base.{PartialImage, PartialImageWithShadow}
import com.ajjpj.adiagram.geometry.APoint


/**
 * @author arno
 */
class ADiagramController (root: DiagramRootContainer, diagram: ADiagram)(implicit digest: Digest) {
  val selections = new SelectionTracker(diagram, root)
  val mouseTracker = new MouseTracker(root, diagram, selections)

  var detailsByElement = Map[AShapeSpec, RenderDetails]()

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
      def render: PartialImageWithShadow = spec.shape.render

      def drawOnCanvas(i: PartialImage, c: Canvas) {
        c.setWidth (i.img.getWidth)
        c.setHeight (i.img.getHeight)
        c.getGraphicsContext2D.clearRect(0, 0, c.getWidth, c.getHeight) //TODO is there a better way for this?
        c.getGraphicsContext2D.drawImage(i.img, 0, 0)
      }

      val snapshotCurrentlyRendered = spec.snapshot
      if(detailsByElement(spec).snapshot != snapshotCurrentlyRendered) {
        val counterSnapshot = spec.changeCounter

        JavaFxHelper.inBackground(render, (pi: PartialImageWithShadow) => {
          val newShapeOffset = pi.shape.renderOffset
          var newShadowOffset: APoint = APoint.ZERO

          detailsByElement.get(spec) match {
            case Some(details) if details.changeCounter <= counterSnapshot => //TODO change '<=' to '<' : Problem is repaint of bound lines
              drawOnCanvas(pi.shape, details.shapeCanvas)
              pi.shadow match {
                case Some(sh) =>
                  newShadowOffset = sh.renderOffset
                  drawOnCanvas(sh, details.shadowCanvas)
                case None =>
                  details.shadowCanvas.setWidth(0)
                  details.shadowCanvas.setHeight(0)
              }

              detailsByElement += (spec -> details.copy(snapshot = snapshotCurrentlyRendered, changeCounter = counterSnapshot, shapeOffset = newShapeOffset, shadowOffset = newShadowOffset))
            case _ => // do nothing - the element was removed from the diagram, or a later change was rendered by now
          }
          //TODO replace 'new Digest()' with something more appropriate
        })(new Digest()) // no changes are performed in this UI callback --> no need for events to be triggered
      }
    }

    def refreshPos(spec: AShapeSpec) = {
      val details = detailsByElement(spec)
      details.shapeCanvas.setLayoutX (spec.pos.x + details.shapeOffset.x)
      details.shapeCanvas.setLayoutY (spec.pos.y + details.shapeOffset.y)
      details.shadowCanvas.setLayoutX (spec.pos.x + details.shadowOffset.x)
      details.shadowCanvas.setLayoutY (spec.pos.y + details.shadowOffset.y)
    }

    // deal with added and removed elements
    detailsByElement.keys.filterNot(diagram.elements.       contains).foreach(unregisterElement)
    diagram.elements.     filterNot(detailsByElement.keySet.contains).foreach(registerElement)

    // update changed elements
    //TODO work with snapshots, re-render only changed elements
    diagram.elements.foreach (render)
    diagram.elements.foreach (refreshPos)
  }

  case class RenderDetails(changeCounter: Int, shapeCanvas: Canvas, shadowCanvas: Canvas, shapeOffset: APoint, shadowOffset: APoint, snapshot: ShapeSpecReRenderSnapshot)
}

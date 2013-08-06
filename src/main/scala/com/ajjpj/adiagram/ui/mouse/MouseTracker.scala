package com.ajjpj.adiagram.ui.mouse

import com.ajjpj.adiagram.model._
import javafx.scene.input.MouseEvent
import com.ajjpj.adiagram.geometry.APoint
import com.ajjpj.adiagram.ui.fw._
import com.ajjpj.adiagram.ui.presentation.{ADiagramController, DiagramRootContainer}
import com.ajjpj.adiagram.ui.{ResizeDirection, AScreenPos, SelectionTracker}
import com.ajjpj.adiagram.model.diagram.{AShapeSpec, ADiagram}


/**
 * @author arno
 */
class MouseTracker (root: DiagramRootContainer, diagram: ADiagram, ctrl: ADiagramController, selections: SelectionTracker)(implicit digest: Digest) {
  import digest.createEventHandler

  implicit def zoom = ctrl.zoom

  private var trackerStrategy: MouseTrackerStrategy = NullTrackerStrategy


  root.addEventHandler(MouseEvent.ANY, handle _)


  //TODO numClicks, left button
  private def handle(evt: MouseEvent) {
    val p = AScreenPos.fromMouseEvent(evt)
    evt.getEventType match {
      case MouseEvent.MOUSE_PRESSED  => onPressed(p)
      case MouseEvent.MOUSE_DRAGGED  => onDragged(p)
      case MouseEvent.MOUSE_RELEASED => onReleased(p)
      case _ =>
    }
  }


  private def onPressed(p: AScreenPos) = targetFor(p) match {
      case BoxResizeHandleTarget(dir) => trackerStrategy = new ResizeTrackerStrategy(dir, p, selections.selectedShapes)
      case LineHandleTarget(start)    => trackerStrategy = new LineEndTrackerStrategy(start, selections.singleSelectedLine, p, root, diagram)
      case ShapeTarget(sh)            =>
        val prevSelection = selections.selectedShapes
        selections.setSelection(sh)
        digest.undoRedo.push(new SelectShapeCommand(prevSelection.toList, List(sh)))

        trackerStrategy = new MoveTrackerStrategy(p, selections.selectedShapes)
      case NoMouseTarget =>
        digest.undoRedo.push(new SelectShapeCommand(selections.selectedShapes.toList, Nil))
        selections.clearSelection()

        trackerStrategy = NullTrackerStrategy
    }

  private def onDragged(p: AScreenPos) = trackerStrategy.onDragged(p)
  private def onReleased(p: AScreenPos) {
    onDragged(p)
    trackerStrategy.onReleased(p)
  }

  sealed trait MouseTarget
  case class BoxResizeHandleTarget(dir: ResizeDirection) extends MouseTarget
  case class LineHandleTarget(firstEnd: Boolean) extends MouseTarget
  case class ShapeTarget(shape: AShapeSpec) extends MouseTarget
  case object NoMouseTarget extends MouseTarget

  private def targetFor(p: AScreenPos): MouseTarget = {
    selections.resizeDirFor(p).map(BoxResizeHandleTarget).getOrElse (
    selections.lineEndFor(p).map(LineHandleTarget).getOrElse (
    shapeFor(p.toModel).map(ShapeTarget).getOrElse (
    NoMouseTarget
    )))
  }

  private def shapeFor(p: APoint) = diagram.elements.find(_ contains p) //TODO deal with Z appropriately

  class SelectShapeCommand(prevSelection: List[AShapeSpec], newSelection: List[AShapeSpec]) extends Command {
    override def name = "Select Shape"
    override def isNop = prevSelection == newSelection

    override def undo() = selections.setSelection(prevSelection)
    override def redo() = selections.setSelection(newSelection)
  }
}


package com.ajjpj.adiagram.ui.fw

import com.ajjpj.adiagram.model.{ALineSpec, AShapeSpec, ADiagram}
import javafx.scene.input.MouseEvent
import com.ajjpj.adiagram.geometry.APoint


/**
 * @author arno
 */
class MouseTracker (root: DiagramRootContainer, diagram: ADiagram, selections: SelectionTracker)(implicit digest: Digest) {
  import digest.createEventHandler

  private var initialSelectOnly: Boolean = _

  private var dragState: Option[DragState] = None
  private var resizeState: Option[ResizeState] = None
  private var lineEndDragState: Option[LineEndDragState] = None

  root.addEventHandler(MouseEvent.ANY, handle _)


  //TODO numClicks, left button
  private def handle(evt: MouseEvent) {
    val p = APoint(evt.getX, evt.getY)
    evt.getEventType match {
      case MouseEvent.MOUSE_PRESSED  => onPressed(p)
      case MouseEvent.MOUSE_DRAGGED  => onDragged(p)
      case MouseEvent.MOUSE_RELEASED => onReleased(p)
      case _ =>
    }
  }


  private def onPressed(p: APoint) = targetFor(p) match {
      case BoxResizeHandleTarget(dir) =>
        resizeState = Some(ResizeState(dir, p, p))
      case LineHandleTarget(start) =>
        lineEndDragState = Some(LineEndDragState(start, p, p))
      case ShapeTarget(sh) =>
        initialSelectOnly = true
        dragState = Some(DragState(p, p))

        val prevSelection = selections.selectedShapes
        selections.setSelection(sh)
        digest.undoRedo.push(new SelectShapeCommand(prevSelection, List(sh)))
      case NoMouseTarget =>
        if(! selections.selectedShapes.isEmpty) {
          digest.undoRedo.push(new SelectShapeCommand(selections.selectedShapes, Nil))
        }
        selections.clearSelection()
    }

  private def onDragged(p: APoint) {
    resizeState match {
      case Some(s) => onDraggedBoxHandle(s, p)
      case None =>
    }

    lineEndDragState match {
      case Some(s) => onDraggedLineEndHandle(s, p)
      case None =>
    }

    dragState match {
      case Some(s) =>
        if(initialSelectOnly && s.initialPos.distanceTo(p) >= SystemConfiguration.selectToDragThreshold) {
          initialSelectOnly = false
        }
        if(!initialSelectOnly) {
          onDraggedShape(s, p)
        }
      case None =>
    }
  }

  private def doDrag(shape: AShapeSpec, dir: ResizeDirection, delta: APoint) = {
    //TODO limit resizing - maintain a minimum size
    val deltaPosX   = if(dir.left)  delta.x else 0.0
    val deltaPosY   = if(dir.top)   delta.y else 0.0
    val deltaWidth  = if(dir.left) -delta.x else if(dir.right)  delta.x else 0.0
    val deltaHeight = if(dir.top)  -delta.y else if(dir.bottom) delta.y else 0.0

    shape.moveBy ((deltaPosX, deltaPosY))
    shape.atomicUpdate {
      shape.resizeBy ((deltaWidth, deltaHeight))
    }
  }

  private def onReleased(p: APoint) {
    onDragged(p)

    resizeState match {
      case Some(s) => digest.undoRedo.push(new ResizeCommand(selections.selectedShapes, s.dir, p - s.initialPos))
      case None =>
    }

    lineEndDragState match {
      case Some(s) => digest.undoRedo.push(new LineEndMoveCommand(selections.singleSelectedLine, s.isStartEnd, p - s.initialPos))
      case None =>
    }

    dragState match {
      case Some(s) =>
        if(! initialSelectOnly) {
          digest.undoRedo.push(new MoveCommand(selections.selectedShapes, p - s.initialPos))
        }
      case None =>
    }

    dragState = None
    resizeState = None
    lineEndDragState = None
    initialSelectOnly = false
  }

  private def onDraggedBoxHandle(s: ResizeState, p: APoint) {
    val delta = p - s.prevPos
    selections.selectedShapes.foreach(doDrag(_, s.dir, delta))
    resizeState = Some(s.copy(prevPos = p))
  }

  private def onDraggedLineEndHandle(s: LineEndDragState, p: APoint) {
    val delta = p - s.prevPos
    val lineSpec = selections.singleSelectedLine

    lineSpec.atomicUpdate {
      if(s.isStartEnd)
        lineSpec.p0 += delta
      else
        lineSpec.p1 += delta
    }
    lineEndDragState = Some(s.copy(prevPos = p))
  }

  private def onDraggedShape(s: DragState, p: APoint) {
    val delta = p - s.prevPos
    selections.selectedShapes.foreach(shape => {shape.moveBy(delta)})
    dragState = Some(s.copy(prevPos = p))
  }



  case class SelectShapeCommand(prevSelection: Iterable[AShapeSpec], newSelection: Iterable[AShapeSpec]) extends Command {
    def name = "Select"
    def undo() = selections.setSelection(prevSelection)
    def redo() = selections.setSelection(newSelection)
  }

  class LineEndMoveCommand(lineSpec: ALineSpec, isStartEnd: Boolean, deltaSnapshot: APoint) extends Command {
    def name = "Move Line End"
    def undo() = lineSpec.atomicUpdate {if(isStartEnd) lineSpec.p0 -= deltaSnapshot else lineSpec.p1 -= deltaSnapshot}
    def redo() = lineSpec.atomicUpdate {if(isStartEnd) lineSpec.p0 += deltaSnapshot else lineSpec.p1 += deltaSnapshot}
  }

  class ResizeCommand(selSnapshot: Iterable[AShapeSpec], dirSnapshot: ResizeDirection, deltaSnapshot: APoint) extends Command {
    def name = "Resize" //TODO add type of shape
    def undo() {selSnapshot.foreach(doDrag(_, dirSnapshot, deltaSnapshot.inverse))}
    def redo() {selSnapshot.foreach(doDrag(_, dirSnapshot, deltaSnapshot))}
  }

  class MoveCommand(selSnapshot: Iterable[AShapeSpec], deltaSnapshot: APoint) extends Command {
    def name = "Move" //TODO add type of shape
    def undo() {selSnapshot.foreach(sh => sh.moveBy(deltaSnapshot.inverse))}
    def redo() {selSnapshot.foreach(sh => sh.moveBy(deltaSnapshot))}
  }

  case class ResizeState(dir: ResizeDirection, initialPos: APoint, prevPos: APoint)
  case class DragState(initialPos: APoint, prevPos: APoint)
  case class LineEndDragState(isStartEnd: Boolean, initialPos: APoint, prevPos: APoint)


  sealed trait MouseTarget
  case class BoxResizeHandleTarget(dir: ResizeDirection) extends MouseTarget
  case class LineHandleTarget(firstEnd: Boolean) extends MouseTarget
  case class ShapeTarget(shape: AShapeSpec) extends MouseTarget
  case object NoMouseTarget extends MouseTarget

  private def targetFor(p: APoint): MouseTarget = {
    selections.resizeDirFor(p).map(BoxResizeHandleTarget).getOrElse (
    selections.lineEndFor(p).map(LineHandleTarget).getOrElse (
    shapeFor(p).map(ShapeTarget).getOrElse (
    NoMouseTarget
    )))
  }

  private def shapeFor(p: APoint) = diagram.elements.find(_ contains p) //TODO deal with Z appropriately
}


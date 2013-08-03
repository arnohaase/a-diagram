package com.ajjpj.adiagram.ui

import com.ajjpj.adiagram.model._
import javafx.scene.input.MouseEvent
import com.ajjpj.adiagram.geometry.APoint
import com.ajjpj.adiagram.ui.fw._
import scala.Some
import com.ajjpj.adiagram.ui.presentation.DiagramRootContainer


/**
 * @author arno
 */
class MouseTracker (root: DiagramRootContainer, diagram: ADiagram, selections: SelectionTracker)(implicit digest: Digest) {
  import digest.createEventHandler

  private var initialSelectOnly: Boolean = _

  private var dragState: Option[DragState] = None
  private var resizeState: Option[ResizeState] = None
  private var lineEndDragState: Option[LineEndDragState] = None

  private var lineDragOverlay: Option[DragTargetOverlay] = None

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
        lineDragOverlay = Some(new DragTargetOverlay(diagram))
        root.showOverlay(lineDragOverlay.get)
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
    lineDragOverlay match {
      case Some(o) => o.onMouseMoved(p)
      case None =>
    }

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
      case Some(s) =>
        lineDragOverlay match {
          case Some(o) if o.activeItem.isDefined =>
            val line = selections.singleSelectedLine
            val oldStartBinding = line.p0Source
            val oldEndBinding   = line.p1Source

            if(s.isStartEnd)
              line.atomicUpdate { line.bindStartPoint(o.activeItem.get.asInstanceOf[ABoxSpec]) }
            else
              line atomicUpdate { line.bindEndPoint(o.activeItem.get.asInstanceOf[ABoxSpec]) }
            digest.undoRedo.push(new LineEndMoveCommand(selections.singleSelectedLine, s.isStartEnd, p - s.initialPos))
            digest.undoRedo.push(new BindLineEndsCommand (line, oldStartBinding, line.p0Source, oldEndBinding, line.p1Source))
          case Some(o) =>
            val line = selections.singleSelectedLine
            val oldStartBinding = line.p0Source
            val oldEndBinding   = line.p1Source

            if(s.isStartEnd)
              line.atomicUpdate { line.unbindStartPoint() }
            else
              line.atomicUpdate { line.unbindEndPoint() }
            digest.undoRedo.push(new LineEndMoveCommand(selections.singleSelectedLine, s.isStartEnd, p - s.initialPos))
            digest.undoRedo.push(new BindLineEndsCommand (line, oldStartBinding, line.p0Source, oldEndBinding, line.p1Source))
          case None =>
            digest.undoRedo.push(new LineEndMoveCommand(selections.singleSelectedLine, s.isStartEnd, p - s.initialPos))
        }
      case None =>
    }

    dragState match {
      case Some(s) =>
        if(! initialSelectOnly) {
          selections.singleSelection[ALineSpec] match { //TODO mixed multi selection - unbind or bind?!
            case Some(line) =>
              digest.undoRedo.push(new BindLineEndsCommand (line, line.p0Source, new LiteralPosSource(line.p0Source.pos), line.p1Source, new LiteralPosSource(line.p1Source.pos)))
              line.unbindStartPoint()
              line.unbindEndPoint()
            case _ =>
          }
          digest.undoRedo.push(new MoveCommand(selections.selectedShapes, p - s.initialPos))
        }
      case None =>
    }

    lineDragOverlay = None
    root.clearOverlay()

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
        lineSpec.p0Source += delta
      else
        lineSpec.p1Source += delta
    }
    lineEndDragState = Some(s.copy(prevPos = p))
  }

  private def onDraggedShape(s: DragState, p: APoint) {
    val delta = p - s.prevPos
    selections.selectedShapes.foreach(shape => {shape.moveBy(delta)})
    dragState = Some(s.copy(prevPos = p))
  }


  case class BindLineEndsCommand(line: ALineSpec,
                                 oldStartBinding: PosSource, newStartBinding: PosSource,
                                 oldEndBinding:   PosSource, newEndBinding:   PosSource) extends Command {
    def name = "Attach Line Ends"
    def isNop = oldStartBinding == newStartBinding && oldEndBinding == newEndBinding

    def undo() {
      line.p0Source = oldStartBinding
      line.p1Source = oldEndBinding
    }

    def redo() {
      line.p0Source = newStartBinding
      line.p1Source = newEndBinding
    }
  }

  case class SelectShapeCommand(prevSelection: Iterable[AShapeSpec], newSelection: Iterable[AShapeSpec]) extends Command {
    def name = "Select"
    def isNop = prevSelection == newSelection
    def undo() = selections.setSelection(prevSelection)
    def redo() = selections.setSelection(newSelection)
  }

  class LineEndMoveCommand(lineSpec: ALineSpec, isStartEnd: Boolean, deltaSnapshot: APoint) extends Command {
    def name = "Move Line End"
    def isNop = deltaSnapshot == APoint.ZERO
    def undo() = lineSpec.atomicUpdate {if(isStartEnd) lineSpec.p0Source -= deltaSnapshot else lineSpec.p1Source -= deltaSnapshot}
    def redo() = lineSpec.atomicUpdate {if(isStartEnd) lineSpec.p0Source += deltaSnapshot else lineSpec.p1Source += deltaSnapshot}
  }

  class ResizeCommand(selSnapshot: Iterable[AShapeSpec], dirSnapshot: ResizeDirection, deltaSnapshot: APoint) extends Command {
    def name = "Resize" //TODO add type of shape
    def isNop = deltaSnapshot == APoint.ZERO
    def undo() {selSnapshot.foreach(doDrag(_, dirSnapshot, deltaSnapshot.inverse))}
    def redo() {selSnapshot.foreach(doDrag(_, dirSnapshot, deltaSnapshot))}
  }

  class MoveCommand(selSnapshot: Iterable[AShapeSpec], deltaSnapshot: APoint) extends Command {
    def name = "Move" //TODO add type of shape
    def isNop = deltaSnapshot == APoint.ZERO
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


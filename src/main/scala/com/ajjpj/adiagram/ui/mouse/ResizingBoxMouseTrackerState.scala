package com.ajjpj.adiagram.ui.mouse

import com.ajjpj.adiagram.ui.presentation.ADiagramController
import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import com.ajjpj.adiagram.ui.{ResizeDirection, AScreenPos}
import com.ajjpj.adiagram.model.diagram.AShapeSpec
import com.ajjpj.adiagram.geometry.APoint


/**
 * @author arno
 */
private[mouse] class ResizingBoxMouseTrackerState(ctrl: ADiagramController, stateMachine: MouseTrackerSM, handles: BoxHandles, dir: ResizeDirection, initialPos: AScreenPos)(implicit digest: Digest)
  extends NullMouseTrackerState(ctrl, stateMachine) {

  implicit def zoom = ctrl.zoom

  private var prevPos = initialPos

  override def onDragged(p: AScreenPos) {
    val delta = p - prevPos
    ctrl.selections.selectedShapes.foreach(doResize(_, delta.toModel))
    prevPos = p
  }

  override def onReleased(p: AScreenPos) {
    digest.undoRedo.push(new ResizeCommand(ctrl.selections.selectedShapes, dir, (p - initialPos).toModel))
    stateMachine.changeState(new DefaultMouseTrackerState(ctrl, stateMachine))
  }

  override def cleanup() {
    handles.unbind()
  }

  private def doResize(shape: AShapeSpec, delta: APoint) = {
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

  class ResizeCommand(selSnapshot: Traversable[AShapeSpec], dirSnapshot: ResizeDirection, deltaSnapshot: APoint) extends Command {
    def name = "Resize" //TODO add type of shape
    def isNop = deltaSnapshot == APoint.ZERO
    def undo() {selSnapshot.foreach(doResize(_, deltaSnapshot.inverse))}
    def redo() {selSnapshot.foreach(doResize(_, deltaSnapshot))}
  }

}

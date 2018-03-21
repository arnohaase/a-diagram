package com.ajjpj.adiagram_.ui.mouse

import com.ajjpj.adiagram_.ui.fw.{Command, Digest}
import com.ajjpj.adiagram_.ui.{ADiagramController, AScreenPos}
import com.ajjpj.adiagram_.model.diagram.{PosSource, ALineSpec, ABoxSpec}

/**
 * @author arno
 */
private[mouse] class DraggingLineEndMouseTrackerState(ctrl: ADiagramController, stateMachine: MouseTrackerSM, handles: LineHandles, isStartEnd: Boolean, initialPos: AScreenPos)(implicit digest: Digest)
  extends NullMouseTrackerState(ctrl, stateMachine) {

  private implicit def zoom = ctrl.zoom
  private val line = ctrl.selections.singleSelectedLine

  private def binding = if(isStartEnd) line.p0Source else line.p1Source

  val initialBinding = binding
  var prevPos = initialPos

  private val overlay = new DragTargetOverlay(ctrl.diagram, ctrl.zoom)
  ctrl.root.setOverlay(overlay)

  override def onDragged(p: AScreenPos) {
    overlay.onMouseMoved(p)

    val delta = (p - prevPos).toModel
    line.atomicUpdate {
      if(isStartEnd)
        line.p0Source += delta
      else
        line.p1Source += delta
    }
    prevPos = p
  }


  override def onReleased(p: AScreenPos) {
    if(overlay.activeItem.isDefined) {
      if(isStartEnd)
        line.atomicUpdate { line.bindStartPoint(overlay.activeItem.get.asInstanceOf[ABoxSpec]) }
      else
        line atomicUpdate { line.bindEndPoint(overlay.activeItem.get.asInstanceOf[ABoxSpec]) }
    }
    else {
      if(isStartEnd)
        line.atomicUpdate { line.unbindStartPoint() }
      else
        line.atomicUpdate { line.unbindEndPoint() }
    }
    digest.undoRedo.push(new MoveLineEndCommand (line, isStartEnd, initialBinding, binding))
    stateMachine.changeState(new DefaultMouseTrackerState(ctrl, stateMachine))
  }

  override def cleanup() {
    handles.unbind()
    ctrl.root.clearOverlay()
  }

  case class MoveLineEndCommand(line: ALineSpec, isStartEnd: Boolean, oldBinding: PosSource, newBinding: PosSource) extends Command {
    def name = "Move Line End"
    def isNop = oldBinding == newBinding

    def undo() = if(isStartEnd) line.p0Source = oldBinding else line.p1Source = oldBinding
    def redo() = if(isStartEnd) line.p0Source = newBinding else line.p1Source = newBinding
  }

}

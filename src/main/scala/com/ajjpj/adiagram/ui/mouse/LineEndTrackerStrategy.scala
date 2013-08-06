package com.ajjpj.adiagram.ui.mouse

import com.ajjpj.adiagram.ui.{Zoom, DragTargetOverlay, AScreenPos}
import com.ajjpj.adiagram.ui.presentation.DiagramRootContainer
import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import com.ajjpj.adiagram.model.diagram.{PosSource, ALineSpec, ADiagram, ABoxSpec}

/**
 * @author arno
 */
private[mouse] class LineEndTrackerStrategy(isStartEnd: Boolean, line: ALineSpec, initialPos: AScreenPos, root: DiagramRootContainer, diagram: ADiagram)(implicit digest: Digest, zoom: Zoom) extends MouseTrackerStrategy {
  private def binding = if(isStartEnd) line.p0Source else line.p1Source

  val initialBinding = binding
  var prevPos = initialPos

  private val overlay = new DragTargetOverlay(diagram, zoom)
  root.showOverlay(overlay)

  def onDragged(p: AScreenPos) {
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


  def onReleased(p: AScreenPos) {
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

    root.clearOverlay()
  }

  case class MoveLineEndCommand(line: ALineSpec, isStartEnd: Boolean, oldBinding: PosSource, newBinding: PosSource) extends Command {
    def name = "Move Line End"
    def isNop = oldBinding == newBinding

    def undo() = if(isStartEnd) line.p0Source = oldBinding else line.p1Source = oldBinding
    def redo() = if(isStartEnd) line.p0Source = newBinding else line.p1Source = newBinding
  }
}

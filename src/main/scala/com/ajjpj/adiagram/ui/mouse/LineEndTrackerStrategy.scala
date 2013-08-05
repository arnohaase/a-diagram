package com.ajjpj.adiagram.ui.mouse

import com.ajjpj.adiagram.ui.{Zoom, DragTargetOverlay, AScreenPos}
import com.ajjpj.adiagram.model.{PosSource, ABoxSpec, ALineSpec, ADiagram}
import com.ajjpj.adiagram.ui.presentation.DiagramRootContainer
import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import com.ajjpj.adiagram.geometry.APoint

/**
 * @author arno
 */
private[mouse] class LineEndTrackerStrategy(isStartEnd: Boolean, line: ALineSpec, initialPos: AScreenPos, root: DiagramRootContainer, diagram: ADiagram)(implicit digest: Digest, zoom: Zoom) extends MouseTrackerStrategy {
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
        val oldStartBinding = line.p0Source
        val oldEndBinding   = line.p1Source

        if(isStartEnd)
          line.atomicUpdate { line.bindStartPoint(overlay.activeItem.get.asInstanceOf[ABoxSpec]) }
        else
          line atomicUpdate { line.bindEndPoint(overlay.activeItem.get.asInstanceOf[ABoxSpec]) }

        digest.undoRedo.push(new LineEndMoveCommand(line, isStartEnd, (p - initialPos).toModel))
        digest.undoRedo.push(new BindLineEndsCommand (line, oldStartBinding, line.p0Source, oldEndBinding, line.p1Source))
    }
    else {
        val oldStartBinding = line.p0Source
        val oldEndBinding   = line.p1Source

        if(isStartEnd)
          line.atomicUpdate { line.unbindStartPoint() }
        else
          line.atomicUpdate { line.unbindEndPoint() }
        digest.undoRedo.push(new LineEndMoveCommand(line, isStartEnd, (p - initialPos).toModel))
        digest.undoRedo.push(new BindLineEndsCommand (line, oldStartBinding, line.p0Source, oldEndBinding, line.p1Source))
    }

    root.clearOverlay()
  }

  class LineEndMoveCommand(lineSpec: ALineSpec, isStartEnd: Boolean, deltaSnapshot: APoint) extends Command {
    def name = "Move Line End"
    def isNop = deltaSnapshot == APoint.ZERO
    def undo() = lineSpec.atomicUpdate {if(isStartEnd) lineSpec.p0Source -= deltaSnapshot else lineSpec.p1Source -= deltaSnapshot}
    def redo() = lineSpec.atomicUpdate {if(isStartEnd) lineSpec.p0Source += deltaSnapshot else lineSpec.p1Source += deltaSnapshot}
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
}

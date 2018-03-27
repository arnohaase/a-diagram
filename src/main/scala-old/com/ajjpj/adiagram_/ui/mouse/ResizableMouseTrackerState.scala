package com.ajjpj.adiagram_.ui.mouse

import com.ajjpj.adiagram_.ui.AScreenPos


/**
 * @author arno
 */
private[mouse] trait ResizableMouseTrackerState extends MouseTrackerState {
  private val boxHandles = new BoxHandles(ctrl)
  private var keepBoxHandles = false

  private val lineHandles = new LineHandles(ctrl)
  private var keepLineHandles = false


  abstract override def onPressed(p: AScreenPos) {
    (boxHandles.resizeDirFor(p), lineHandles.lineEndFor(p)) match {
      case (Some(dir), _) =>
        keepBoxHandles = true
        stateMachine.changeState(new ResizingBoxMouseTrackerState(ctrl, stateMachine, boxHandles, dir, p))
      case (_, Some(end)) =>
        keepLineHandles = true
        stateMachine.changeState(new DraggingLineEndMouseTrackerState(ctrl, stateMachine, lineHandles, end, p))
      case _ =>
        super.onPressed(p)
    }
  }

  abstract override def cleanup() {
    super.cleanup()

    if(!keepBoxHandles) boxHandles.unbind()
    if(!keepLineHandles) lineHandles.unbind()
  }
}


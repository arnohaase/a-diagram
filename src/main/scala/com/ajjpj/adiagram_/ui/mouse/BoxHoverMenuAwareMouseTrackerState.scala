package com.ajjpj.adiagram_.ui.mouse

import com.ajjpj.adiagram_.ui.AScreenPos
import com.ajjpj.adiagram_.model.diagram.ABoxSpec

/**
 * @author arno
 */
private[mouse] trait BoxHoverMenuAwareMouseTrackerState extends MouseTrackerState {
  abstract override def onMoved(p: AScreenPos) {
    shapeFor(p.toModel(ctrl.zoom)) match {
      case Some(box: ABoxSpec) => stateMachine.changeState(new BoxHoverMenuMouseTrackerState(ctrl, stateMachine, box))
      case _ => super.onMoved(p)
    }
  }
}

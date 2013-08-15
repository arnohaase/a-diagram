package com.ajjpj.adiagram.ui.mouse

import com.ajjpj.adiagram.ui.AScreenPos
import com.ajjpj.adiagram.geometry.APoint
import com.ajjpj.adiagram.model.diagram.AShapeSpec
import com.ajjpj.adiagram.ui.fw.Command

/**
 * This state allows selecting a shape. It is intended to be used as a mix-in stackable trait.
 *
 * @author arno
 */
private[mouse] trait SelectableMouseTrackerState extends MouseTrackerState {
  private implicit def zoom = ctrl.zoom

  override abstract def onPressed(p: AScreenPos) = shapeFor(p.toModel) match {
    case Some(shape: AShapeSpec) =>
      changeSelection(shape)
      stateMachine.changeState(new DraggingMouseTrackerState(p, ctrl, stateMachine))
    case _ => super.onPressed(p)
  }

  private def changeSelection(shape: AShapeSpec) {
    val prevSelection = ctrl.selections.selectedShapes
    ctrl.selections.setSelection(shape)
    digest.undoRedo.push(new SelectShapeCommand(prevSelection.toList, List(shape)))
  }

  class SelectShapeCommand(prevSelection: List[AShapeSpec], newSelection: List[AShapeSpec]) extends Command {
    override def name = "Select Shape"
    override def isNop = prevSelection == newSelection

    override def undo() = ctrl.selections.setSelection(prevSelection)
    override def redo() = ctrl.selections.setSelection(newSelection)
  }
}

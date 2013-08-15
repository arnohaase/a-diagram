package com.ajjpj.adiagram.ui.mouse

import com.ajjpj.adiagram.ui.presentation.ADiagramController
import com.ajjpj.adiagram.ui.fw.{JavaFxHelper, Digest}
import javafx.scene.layout.VBox
import javafx.scene.image.ImageView
import com.ajjpj.adiagram.model.diagram.{LiteralPosSource, BoxPosSource, ALineSpec, ABoxSpec}
import com.ajjpj.adiagram.ui.AScreenPos

/**
 * @author arno
 */
private[mouse] class BoxHoverMenuMouseTrackerState(ctrl: ADiagramController, stateMachine: MouseTrackerSM, box: ABoxSpec)(implicit digest: Digest) extends DefaultMouseTrackerState(ctrl, stateMachine) {
  implicit def zoom = ctrl.zoom

  val menuBox = new VBox(10)

  val arrowImage = JavaFxHelper.image("arrow.png")
  val arrow = new ImageView(arrowImage)
  menuBox.getChildren.add(arrow)

  //TODO transform to screen coordinates (?)
  menuBox.setLayoutX(box.pos.x + box.dim.width)
  menuBox.setLayoutY(box.pos.y)
  ctrl.root.getChildren.add(menuBox)

  override def onPressed(p: AScreenPos) {
    if(ctrl.root.boundsInRoot(arrow).contains(p)) {
      createNewLine(p)
    }
    else
      super.onPressed(p)
  }

  private def createNewLine(p: AScreenPos) {
    val line = new ALineSpec(None, ctrl.selectedStyles.lineStyle, ctrl.selectedStyles.textStyle, ctrl.selectedStyles.startLineEnd, ctrl.selectedStyles.endLineEnd)

    val boxSource = BoxPosSource(box)
    line.p0Source = boxSource
    line.p1Source = LiteralPosSource(p.toModel)
    boxSource.opposite = () => line.p1Source

    ctrl.diagram += line

    ctrl.selections.setSelection(line)

    val handles = new LineHandles(ctrl)
    stateMachine.changeState(new DraggingLineEndMouseTrackerState(ctrl, stateMachine, handles, false, p))
  }

  override def onMoved(p: AScreenPos) {
    val stillInSameBox = box.contains(p.toModel) || ctrl.root.boundsInRoot(menuBox).contains(p)
    if(stillInSameBox)
      super.onMoved(p)
    else
      stateMachine.changeState(new DefaultMouseTrackerState(ctrl, stateMachine))
  }

  override def cleanup() {
    ctrl.root.getChildren.remove(menuBox)
    super.cleanup()
  }
}

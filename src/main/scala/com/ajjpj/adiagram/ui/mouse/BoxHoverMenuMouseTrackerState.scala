package com.ajjpj.adiagram.ui.mouse

import com.ajjpj.adiagram.ui.fw.{Command, JavaFxHelper, Digest}
import javafx.scene.layout.VBox
import javafx.scene.image.ImageView
import com.ajjpj.adiagram.model.diagram._
import com.ajjpj.adiagram.ui.{ADiagramController, Zoom, AScreenRect, AScreenPos}
import com.ajjpj.adiagram.model.diagram.BoxPosSource
import com.ajjpj.adiagram.model.diagram.LiteralPosSource

/**
 * @author arno
 */
private[mouse] class BoxHoverMenuMouseTrackerState(ctrl: ADiagramController, stateMachine: MouseTrackerSM, box: ABoxSpec)(implicit digest: Digest) extends DefaultMouseTrackerState(ctrl, stateMachine) {
  implicit def zoom = ctrl.zoom

  val menuBox = new VBox(10)

  val arrowImage = JavaFxHelper.image("arrow.png")
  val arrow = new ImageView(arrowImage)
  menuBox.getChildren.add(arrow)

  val boxBounds = AScreenRect(box.boundsForResizing, zoom)
  menuBox.setLayoutX(boxBounds.topRight.x)
  menuBox.setLayoutY(boxBounds.topRight.y)
  ctrl.root.getChildren.add(menuBox)

  private val initialZoom = zoom
  private val onZoomChanged = (newZoom: Zoom) => {
    if(initialZoom != newZoom) { // this check is necessary because the digest triggers a 'change' callback initially
      stateMachine.changeState(new DefaultMouseTrackerState(ctrl, stateMachine))
    }
  }
  digest.watch(ctrl.zoom, onZoomChanged)

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

    ctrl.digest.undoRedo.push(new CreateLineCommand(ctrl.selections.selectedShapes, line))

    ctrl.diagram += line
    ctrl.selections.setSelection(line)

    val handles = new LineHandles(ctrl)
    stateMachine.changeState(new DraggingLineEndMouseTrackerState(ctrl, stateMachine, handles, false, p))
  }

  case class CreateLineCommand(sel: Set[AShapeSpec], line: ALineSpec) extends Command {
    def name = "Create Line"
    def isNop = false

    def undo() {
      ctrl.diagram -= line
      ctrl.selections.setSelection(sel)
    }

    def redo() {
      ctrl.diagram += line
      ctrl.selections.setSelection(line)
    }
  }

  override def onMoved(p: AScreenPos) {
    val stillInSameBox = box.contains(p.toModel) || ctrl.root.boundsInRoot(menuBox).contains(p)
    if(stillInSameBox)
      super.onMoved(p)
    else
      stateMachine.changeState(new DefaultMouseTrackerState(ctrl, stateMachine))
  }

  override def cleanup() {
    digest.unbind(onZoomChanged)
    ctrl.root.getChildren.remove(menuBox)
    super.cleanup()
  }
}

package com.ajjpj.adiagram.ui.mouse

import com.ajjpj.adiagram.ui.fw.Digest
import javafx.scene.input.MouseEvent
import com.ajjpj.adiagram.ui.{ADiagramController, AScreenPos}
import com.ajjpj.adiagram.geometry.APoint

/**
 * @author arno
 */
class MouseTracker (ctrl: ADiagramController)(implicit digest: Digest) extends MouseTrackerSM {
  import digest.createEventHandler

  private var curState: MouseTrackerState = new DefaultMouseTrackerState(ctrl, this)
  override def changeState(newState: MouseTrackerState) = {
    curState.cleanup()
    curState = newState
  }

  ctrl.root.addEventHandler(MouseEvent.ANY, handle _)

  private def handle(evt: MouseEvent) {
    val p = AScreenPos.fromMouseEvent(evt)
    evt.getEventType match {
      case MouseEvent.MOUSE_MOVED    => curState.onMoved(p)
      case MouseEvent.MOUSE_PRESSED  => curState.onPressed(p)
      case MouseEvent.MOUSE_DRAGGED  => curState.onDragged(p)
      case MouseEvent.MOUSE_RELEASED => curState.onReleased(p)
      case _ =>
    }
  }
}

trait MouseTrackerState {
  def onMoved(p: AScreenPos)
  def onPressed(p: AScreenPos)
  def onDragged(p: AScreenPos)
  def onReleased(p: AScreenPos)

  def cleanup()

  protected def shapeFor(p: APoint) = ctrl.diagram.elements.find(_ contains p) //TODO deal with Z appropriately


  def stateMachine: MouseTrackerSM
  def ctrl: ADiagramController
  implicit def digest: Digest
}

trait MouseTrackerSM {
  def changeState(newState: MouseTrackerState)
}

class NullMouseTrackerState(val ctrl: ADiagramController, val stateMachine: MouseTrackerSM)(implicit val digest: Digest) extends MouseTrackerState {
  def onMoved(p: AScreenPos) {}
  def onPressed(p: AScreenPos) {}
  def onDragged(p: AScreenPos) {}
  def onReleased(p: AScreenPos) {}

  def cleanup() {}
}

class DefaultMouseTrackerState(ctrl: ADiagramController, stateMachine: MouseTrackerSM)(implicit digest: Digest)
  extends NullMouseTrackerState(ctrl, stateMachine)
  with SelectableMouseTrackerState
  with ResizableMouseTrackerState
  with BoxHoverMenuAwareMouseTrackerState


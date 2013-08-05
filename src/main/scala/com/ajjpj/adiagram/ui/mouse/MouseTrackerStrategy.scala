package com.ajjpj.adiagram.ui.mouse

import com.ajjpj.adiagram.ui.AScreenPos

/**
 * @author arno
 */
private[mouse] trait MouseTrackerStrategy {
  def onDragged(p: AScreenPos)
  def onReleased(p: AScreenPos)
}

private[mouse] object NullTrackerStrategy extends MouseTrackerStrategy {
  def onDragged(p: AScreenPos) {}
  def onReleased(p: AScreenPos) {}
}
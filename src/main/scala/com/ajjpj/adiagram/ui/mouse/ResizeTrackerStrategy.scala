package com.ajjpj.adiagram.ui.mouse

import com.ajjpj.adiagram.ui.{Zoom, ResizeDirection, AScreenPos}
import com.ajjpj.adiagram.geometry.APoint
import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import com.ajjpj.adiagram.model.diagram.AShapeSpec

/**
 * @author arno
 */
private[mouse] class ResizeTrackerStrategy(dir: ResizeDirection, initialPos: AScreenPos, selectedShapes: Traversable[AShapeSpec])(implicit digest: Digest, zoom: Zoom) extends MouseTrackerStrategy {
  var prevPos = initialPos

  def onDragged(p: AScreenPos) {
    val delta = p - prevPos
    selectedShapes.foreach(doResize(_, dir, delta.toModel))
    prevPos = p
  }

  private def doResize(shape: AShapeSpec, dir: ResizeDirection, delta: APoint) = {
    //TODO limit resizing - maintain a minimum size
    val deltaPosX   = if(dir.left)  delta.x else 0.0
    val deltaPosY   = if(dir.top)   delta.y else 0.0
    val deltaWidth  = if(dir.left) -delta.x else if(dir.right)  delta.x else 0.0
    val deltaHeight = if(dir.top)  -delta.y else if(dir.bottom) delta.y else 0.0

    shape.moveBy ((deltaPosX, deltaPosY))
    shape.atomicUpdate {
      shape.resizeBy ((deltaWidth, deltaHeight))
    }
  }


  def onReleased(p: AScreenPos) {
    digest.undoRedo.push(new ResizeCommand(selectedShapes, dir, (p - initialPos).toModel))
  }

  class ResizeCommand(selSnapshot: Traversable[AShapeSpec], dirSnapshot: ResizeDirection, deltaSnapshot: APoint) extends Command {
    def name = "Resize" //TODO add type of shape
    def isNop = deltaSnapshot == APoint.ZERO
    def undo() {selSnapshot.foreach(doResize(_, dirSnapshot, deltaSnapshot.inverse))}
    def redo() {selSnapshot.foreach(doResize(_, dirSnapshot, deltaSnapshot))}
  }
}

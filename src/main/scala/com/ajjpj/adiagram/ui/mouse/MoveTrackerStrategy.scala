package com.ajjpj.adiagram.ui.mouse

import com.ajjpj.adiagram.ui.{Zoom, AScreenPos}
import com.ajjpj.adiagram.ui.fw.{Command, Digest, SystemConfiguration}
import com.ajjpj.adiagram.geometry.APoint
import com.ajjpj.adiagram.model.diagram.{AShapeSpec, PosSource, ALineSpec}

/**
 * @author arno
 */
private[mouse] class MoveTrackerStrategy(initialPos: AScreenPos, selectedShapes: Traversable[AShapeSpec])(implicit digest: Digest, zoom: Zoom) extends MouseTrackerStrategy {
  private var initialSelectOnly = true
  private var prevPos = initialPos

  private val initialSnapshot = selectedShapes.map(snapshot)

  def onDragged(p: AScreenPos) {
    if(initialSelectOnly && initialPos.distanceTo(p) >= SystemConfiguration.selectToDragThreshold) {
      initialSelectOnly = false
    }
    if(!initialSelectOnly) {
      val delta = (p - prevPos).toModel
      selectedShapes.foreach(shape => {shape.moveBy(delta)}) // TODO prevent line end unbinding if bound shape is moved as well
      prevPos = p
    }
  }

  def onReleased(p: AScreenPos) {
    if(! initialSelectOnly) {
      digest.undoRedo.push(MoveCommand(initialSnapshot, selectedShapes.map(snapshot)))
    }
  }


  private def snapshot(shape: AShapeSpec) = shape match {
    case line: ALineSpec => new LineSpecSnapshot(line)
    case _               => new PosSpecSnapshot(shape)
  }

  private sealed trait SpecSnapshot {
    def restore()
  }

  private case class LineSpecSnapshot(line: ALineSpec, p0Source: PosSource, p1Source: PosSource) extends SpecSnapshot {
    def this(line: ALineSpec) = this (line, line.p0Source, line.p1Source)
    override def restore() {line.p0Source = p0Source; line.p1Source = p1Source}
  }

  private case class PosSpecSnapshot(shape: AShapeSpec, pos: APoint) extends SpecSnapshot {
    def this(shape: AShapeSpec) = this(shape, shape.pos)
    override def restore() {shape.pos = pos}
  }


  private case class MoveCommand(prevSnapshot: Traversable[SpecSnapshot], newSnapshot: Traversable[SpecSnapshot]) extends Command {
    def name = "Move" //TODO add type of shape

    def isNop: Boolean = false //TODO NOP!!!

    def undo() {prevSnapshot.foreach(_.restore())}
    def redo() {newSnapshot .foreach(_.restore())}
  }
}

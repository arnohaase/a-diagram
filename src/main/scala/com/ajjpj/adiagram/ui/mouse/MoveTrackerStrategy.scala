package com.ajjpj.adiagram.ui.mouse

import com.ajjpj.adiagram.ui.{Zoom, AScreenPos}
import com.ajjpj.adiagram.ui.fw.{Command, Digest, SystemConfiguration}
import com.ajjpj.adiagram.model.{PosSource, LiteralPosSource, ALineSpec, AShapeSpec}
import com.ajjpj.adiagram.geometry.APoint

/**
 * @author arno
 */
private[mouse] class MoveTrackerStrategy(initialPos: AScreenPos, selectedShapes: Traversable[AShapeSpec])(implicit digest: Digest, zoom: Zoom) extends MouseTrackerStrategy {
  private var initialSelectOnly = true
  private var prevPos = initialPos

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
      digest.undoRedo.push(new UnbindLineEndsCommand(lineEndsToUnbind)) //TODO use an initial snapshot of the bindings instead!!
      lineEndsToUnbind.foreach (line => {line.unbindStartPoint(); line.unbindEndPoint()})
      digest.undoRedo.push(new MoveCommand(selectedShapes, (p - initialPos).toModel))
    }
  }

  private def lineEndsToUnbind: List[ALineSpec] = selectedShapes.flatMap(_ match {case x: ALineSpec => Some(x) case _ => None}).toList //TODO filter to exclude those whose line ends bindings are moved too

  class MoveCommand(selSnapshot: Traversable[AShapeSpec], deltaSnapshot: APoint) extends Command {
    def name = "Move" //TODO add type of shape
    def isNop = deltaSnapshot == APoint.ZERO
    def undo() {selSnapshot.foreach(sh => sh.moveBy(deltaSnapshot.inverse))}
    def redo() {selSnapshot.foreach(sh => sh.moveBy(deltaSnapshot))}
  }

  class UnbindLineEndsCommand(lines: List[ALineSpec]) extends Command {
    case class Bindings(line: ALineSpec, oldP0Source: PosSource, oldP1Source: PosSource) {
      def this(line: ALineSpec) = this(line, line.p0Source, line.p1Source)
      def isBound = oldP0Source.isDerived || oldP1Source.isDerived
    }

    val oldBindings = lines.map(line => new Bindings(line)).filter(_.isBound)

    def name = "Unbind Line Ends"
    def isNop = oldBindings.isEmpty

    def undo() {
      oldBindings.foreach(b => {
        b.line.p0Source = b.oldP0Source
        b.line.p1Source = b.oldP1Source
      })
    }

    def redo() {
      oldBindings.foreach(b => {
        b.line.p0Source = new LiteralPosSource(b.line.p0Source.pos)
        b.line.p1Source = new LiteralPosSource(b.line.p1Source.pos)
      })
    }
  }
}

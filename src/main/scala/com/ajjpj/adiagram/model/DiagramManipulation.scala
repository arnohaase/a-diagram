package com.ajjpj.adiagram.model

import com.ajjpj.adiagram.model.diagram._
import com.ajjpj.adiagram.ui.fw.{Digest, Command}
import com.ajjpj.adiagram.model.style._
import com.ajjpj.adiagram.model.diagram.LiteralPosSource
import com.ajjpj.adiagram.ui.ADiagramController


/**
 * @author arno
 */
object DiagramManipulation {
  def addNewBox(diagram: ADiagram, fillStyle: FillStyleSpec, shadowStyle: ShadowStyleSpec, textStyle: TextStyleSpec)(implicit digest: Digest) = {
    //TODO move to the middle of the screen
    val box = new ABoxSpec((100.0, 100.0), (300.0, 100.0), None, fillStyle, shadowStyle, textStyle)
    diagram += box

    digest.undoRedo.push (AddElementCommand (diagram, box))

    box
  }

  def addNewLine(diagram: ADiagram, lineStyle: LineStyleSpec, textStyle: TextStyleSpec, startLineEnd: LineEndSpec, endLineEnd: LineEndSpec)(implicit digest: Digest) = {
    val line = new ALineSpec(None, lineStyle, textStyle, startLineEnd, endLineEnd)
    line.p0Source = LiteralPosSource((100.0, 100.0)) //TODO move to middle of screen
    line.p1Source = LiteralPosSource((200.0, 200.0))
    diagram += line

    digest.undoRedo.push (AddElementCommand (diagram, line))

    line
  }

  def addNewText(diagram: ADiagram, textStyle: TextStyleSpec)(implicit digest: Digest) = {
    //TODO move to middle of screen
    val text = new ATextSpec((100.0, 100.0), (300.0, 100.0), "", textStyle)
    diagram += text

    digest.undoRedo.push (new AddElementCommand(diagram, text))

    text
  }

  def deleteSelection(ctrl: ADiagramController)(implicit digest: Digest) = {
    digest.undoRedo.push(DeleteElementsCommand(ctrl.diagram, ctrl.selections.selectedShapes))
    ctrl.selections.selectedShapes.foreach { ctrl.diagram -= _ }
    ctrl.selections.clearSelection()
  }

  case class AddElementCommand(diagram: ADiagram, shape: AShapeSpec) extends Command {
    def name = "Add Element"
    def isNop = false

    def undo() {diagram -= shape}
    def redo() {diagram += shape}
  }

  case class DeleteElementsCommand(diagram: ADiagram, elements: Traversable[AShapeSpec]) extends Command {
    def name = "Delete Element" + (if (elements.size != 1) "s" else "")
    def isNop = elements.isEmpty

    def undo() {elements.foreach (diagram += _)}
    def redo() {elements.foreach (diagram -= _)}
  }
}

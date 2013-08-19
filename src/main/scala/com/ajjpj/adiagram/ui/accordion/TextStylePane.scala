package com.ajjpj.adiagram.ui.accordion

import com.ajjpj.adiagram.ui.{TextStyleListCell, ADiagramController}
import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import com.ajjpj.adiagram.model.style.TextStyleSpec


/**
  * @author arno
  */
class TextStylePane(ctrl: ADiagramController)(implicit digest: Digest) extends AbstractStylePane[TextStyleSpec, TextStyleListCell, TextStyleChangeCommand](ctrl) {
  def all = ctrl.styleRepository.textStyles
  def snapshot = TextStyleChangeCommand(ctrl, selected.get, selected.get.name, selected.get.name, selected.get.fontSizePixels, selected.get.fontSizePixels)

  textfield("Name:", selected.map(_.name).getOrElse(""), (cmd: TextStyleChangeCommand, newName: String) => cmd.copy(newName = newName))
  textfield("Size[pixel]:", selected.map(_.fontSizePixels.toString).getOrElse(""), (cmd: TextStyleChangeCommand, newSize: String) => cmd.copy(newSize = newSize.toDouble)) //TODO invalid, limit range, ...
}

case class TextStyleChangeCommand(ctrl: ADiagramController, spec: TextStyleSpec, oldName: String, newName: String, oldSize: Double, newSize: Double) extends Command{
  def name = "Change Text Style"
  def isNop = false

  def undo() {
    spec.name = oldName
    spec.fontSizePixels = oldSize
    ctrl.styleRepository.changeCounter += 1
  }
  def redo() {
    spec.name = newName
    spec.fontSizePixels = newSize
    ctrl.styleRepository.changeCounter += 1
  }
}
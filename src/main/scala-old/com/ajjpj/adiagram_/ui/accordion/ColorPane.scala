package com.ajjpj.adiagram_.ui.accordion

import com.ajjpj.adiagram_.ui.fw.{Command, Digest}
import com.ajjpj.adiagram_.ui.{ColorListCell, ADiagramController}
import com.ajjpj.adiagram_.model.style.ColorSpec
import javafx.scene.paint.Color


/**
 * @author arno
 */
class ColorPane(ctrl: ADiagramController)(implicit digest: Digest) extends AbstractStylePane[ColorSpec, ColorListCell, ChangeColorCommand](ctrl) {
  override def all = ctrl.styleRepository.colors
  override def snapshot: ChangeColorCommand = ChangeColorCommand(ctrl, selected.get, selected.get.name, selected.get.name, selected.get.color, selected.get.color)

  textfield("Name:", selected.map(_.name). getOrElse(""),                (cmd: ChangeColorCommand, newName: String) => cmd.copy(newName = newName))
  color("Color:",    selected.map(_.color).getOrElse(Color.TRANSPARENT), (cmd: ChangeColorCommand, newColor: Color) => cmd.copy(newColor = newColor))
}

case class ChangeColorCommand(ctrl: ADiagramController, colorSpec: ColorSpec, oldName: String, newName: String, oldColor: Color, newColor: Color) extends Command {
  def name = "Change Color"
  def isNop = false

  def undo() {
    colorSpec.name = oldName
    colorSpec.color = oldColor
    ctrl.styleRepository.changeCounter += 1
  }
  def redo() {
    colorSpec.name = newName
    colorSpec.color = newColor
    ctrl.styleRepository.changeCounter += 1
  }
}

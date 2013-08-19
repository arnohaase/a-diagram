package com.ajjpj.adiagram.ui.accordion

import com.ajjpj.adiagram.ui.{StyleListCellFactory, ColorListCell, LineStyleListCell, ADiagramController}
import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import com.ajjpj.adiagram.model.style.{ColorSpec, LineStyleSpec}
import javafx.scene.control.ComboBox
import scala.collection.JavaConversions


/**
  * @author arno
  */
class LineStylePane(ctrl: ADiagramController)(implicit digest: Digest) extends AbstractStylePane[LineStyleSpec, LineStyleListCell, LineStyleChangeCommand](ctrl) {
  def all = ctrl.styleRepository.lineStyles
  def snapshot = LineStyleChangeCommand(ctrl, selected.get, selected.get.name, selected.get.name, selected.get.width, selected.get.width, selected.get.colorSpec, selected.get.colorSpec)

  textfield("Name:",  selected.map(_.name).getOrElse(""), (cmd: LineStyleChangeCommand, newName: String) => cmd.copy(newName = newName))
  textfield("Width:", selected.map(_.width.toString).getOrElse(""), (cmd: LineStyleChangeCommand, newWidth: String) => cmd.copy(newWidth = newWidth.toDouble)) //TODO deal with invalid; limit range
  val cmbColor  = combo("Color", ctrl.styleRepository.colors, curColor,  (cmd: LineStyleChangeCommand, newColor: ColorSpec) => cmd.copy(newColor  = newColor))
  cmbColor._2.setButtonCell(new ColorListCell)
  cmbColor._2.setCellFactory(StyleListCellFactory[ColorSpec, ColorListCell])

  val initialized = true
  override def onStyleRepoChanged() {
    if (initialized) {
      def refresh(cmb: ComboBox[ColorSpec]) {
        val sel = cmb.getValue
        cmb.getItems.clear()
        cmb.getItems.addAll(JavaConversions.seqAsJavaList(ctrl.styleRepository.colors))
        cmb.setValue(sel)
      }
      refresh(cmbColor._2)
    }
  }

  private def curColor = selected.map (_.colorSpec).getOrElse (ctrl.styleRepository.colors.iterator.next())
}


case class LineStyleChangeCommand(ctrl: ADiagramController, spec: LineStyleSpec, oldName: String, newName: String, oldWidth: Double, newWidth: Double, oldColor: ColorSpec, newColor: ColorSpec) extends Command {
  def name = "Change Line Style"
  def isNop = false

  def undo() {
    spec.name = oldName
    spec.width = oldWidth
    spec.colorSpec = oldColor
    ctrl.styleRepository.changeCounter += 1
  }
  def redo() {
    spec.name = newName
    spec.width = newWidth
    spec.colorSpec = newColor
    ctrl.styleRepository.changeCounter += 1
  }
}
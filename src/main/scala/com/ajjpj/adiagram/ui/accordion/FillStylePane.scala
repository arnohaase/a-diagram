package com.ajjpj.adiagram.ui.accordion

import com.ajjpj.adiagram.ui.{FillStyleListCell, ADiagramController}
import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import com.ajjpj.adiagram.model.style.FillStyleSpec
import com.ajjpj.adiagram.ui.forms.AbstractForm


/**
 * @author arno
 */
class FillStylePane (ctrl: ADiagramController)(implicit digest: Digest) extends AbstractStylePane[FillStyleSpec, FillStyleListCell, ChangeFillStyleCommand](ctrl) {
  override def all = ctrl.styleRepository.fillStyles
  override def snapshot = ChangeFillStyleCommand()

}

case class ChangeFillStyleCommand() extends Command {
  def name = "Change Fill Style"
  def isNop = false

  def undo() {} //TODO

  def redo() {} //TODO
}

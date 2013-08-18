package com.ajjpj.adiagram.ui.accordion

import com.ajjpj.adiagram.ui.{ColorListCell, StyleListCellFactory, FillStyleListCell, ADiagramController}
import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import com.ajjpj.adiagram.model.style.{ColorSpec, SimpleLinearGradientFillStrategy, SolidFillStrategy, FillStyleSpec}


/**
 * @author arno
 */
class FillStylePane (ctrl: ADiagramController)(implicit digest: Digest) extends AbstractStylePane[FillStyleSpec, FillStyleListCell, ChangeFillStyleCommand](ctrl) {
  import FillStylePane._

  override def all = ctrl.styleRepository.fillStyles
  override def snapshot = ChangeFillStyleCommand(ctrl, selected.get, selected.get.name, selected.get.name, curKind, curKind, curColor, curColor, curColor2, curColor2)

  textfield("Name:", selected.map(_.name).getOrElse(""), (cmd: ChangeFillStyleCommand, newName: String) => cmd.copy(newName = newName))
  val cmbKind = combo("Kind:", List(SOLID, LINEAR), curKind, (cmd: ChangeFillStyleCommand, newKind: String) => cmd.copy(newKind = newKind))

  val cmbColor  = combo("Color",   ctrl.styleRepository.colors, curColor,  (cmd: ChangeFillStyleCommand, newColor: ColorSpec) => cmd.copy(newColor  = newColor))
  val cmbColor2 = combo("Color 2", ctrl.styleRepository.colors, curColor2, (cmd: ChangeFillStyleCommand, newColor: ColorSpec) => cmd.copy(newColor2 = newColor))

  cmbColor._2.setCellFactory(StyleListCellFactory[ColorSpec, ColorListCell])
  cmbColor2._2.setCellFactory(StyleListCellFactory[ColorSpec, ColorListCell])

  digest.bindBoolean(cmbColor2._1.visibleProperty, curKind == LINEAR)
  digest.bindBoolean(cmbColor2._2.visibleProperty, curKind == LINEAR)

  def curKind = selected.map (_.strategy match {
      case _: SolidFillStrategy => SOLID
      case _: SimpleLinearGradientFillStrategy => LINEAR
    }).getOrElse(SOLID)

  def curColor = selected.map (_.strategy match {
    case s: SolidFillStrategy => s.colorSpec
    case s: SimpleLinearGradientFillStrategy => s.colorSpec0
  }).getOrElse (ctrl.styleRepository.colors.iterator.next())

  def curColor2 = selected.map (_.strategy match {
    case s: SolidFillStrategy => ctrl.styleRepository.colors.iterator.next()
    case s: SimpleLinearGradientFillStrategy => s.colorSpec1
  }).getOrElse (ctrl.styleRepository.colors.iterator.next())
}

object FillStylePane {
  val SOLID = "Solid"
  val LINEAR = "Linear Gradient"
}

case class ChangeFillStyleCommand(ctrl: ADiagramController, fillStyleSpec: FillStyleSpec,
                                  oldName: String, newName: String,
                                  oldKind: String, newKind: String,
                                  oldColor: ColorSpec, newColor: ColorSpec,
                                  oldColor2: ColorSpec, newColor2: ColorSpec) extends Command {
  import FillStylePane._

  def name = "Change Fill Style"
  def isNop = false

  def undo() {
    fillStyleSpec.name = oldName
    oldKind match {
      case SOLID => {
        val strategy = new SolidFillStrategy
        strategy.colorSpec = oldColor
        fillStyleSpec.strategy = strategy
      }
      case LINEAR => {
        val strategy = new SimpleLinearGradientFillStrategy
        strategy.colorSpec0 = oldColor
        strategy.colorSpec1 = oldColor2
        fillStyleSpec.strategy = strategy
      }
    }
    ctrl.styleRepository.changeCounter += 1
  }

  def redo() {
    fillStyleSpec.name = newName
    newKind match {
      case SOLID => {
        val strategy = new SolidFillStrategy
        strategy.colorSpec = newColor
        fillStyleSpec.strategy = strategy
      }
      case LINEAR => {
        val strategy = new SimpleLinearGradientFillStrategy
        strategy.colorSpec0 = newColor
        strategy.colorSpec1 = newColor2
        fillStyleSpec.strategy = strategy
      }
    }
    ctrl.styleRepository.changeCounter += 1
  }
}

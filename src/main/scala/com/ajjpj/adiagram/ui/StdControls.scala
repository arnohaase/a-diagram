package com.ajjpj.adiagram.ui

import javafx.scene.control.{ListCell, ListView, ComboBox}
import com.ajjpj.adiagram.model.style._
import javafx.util.Callback

/**
 * @author arno
 */
object StdControls {
  def createFillStyleCombo(ctrl: ADiagramController) = {
    val cmbFill = new ComboBox[FillStyleSpec]
    ctrl.styleRepository.fillStyles.foreach(cmbFill.getItems.add) //TODO bind this --> changes to the repo while the dialog is initialized

    cmbFill.setButtonCell(new FillStyleListCell)
    cmbFill.setCellFactory(new Callback[ListView[FillStyleSpec], ListCell[FillStyleSpec]] {
      override def call(p1: ListView[FillStyleSpec]) = new FillStyleListCell
    })

    cmbFill
  }

  def createLineStyleCombo(ctrl: ADiagramController) = {
    val cmbLineStyle = new ComboBox[LineStyleSpec]

    ctrl.styleRepository.lineStyles.foreach(cmbLineStyle.getItems.add) //TODO bind this

    cmbLineStyle.setButtonCell(new LineStyleListCell)
    cmbLineStyle.setCellFactory(new Callback[ListView[LineStyleSpec], ListCell[LineStyleSpec]] {
      def call(p1: ListView[LineStyleSpec]) = new LineStyleListCell
    })

    cmbLineStyle
  }

  def createLineEndCombo(ctrl: ADiagramController) = {
    val cmbLineEnd = new ComboBox[LineEndSpec]

    ctrl.styleRepository.lineEnds.foreach(cmbLineEnd.getItems.add)
    cmbLineEnd.setButtonCell(new LineEndListCell)

    cmbLineEnd.setCellFactory(new Callback[ListView[LineEndSpec], ListCell[LineEndSpec]] {
      def call(p1: ListView[LineEndSpec]) = new LineEndListCell
    })

    cmbLineEnd
  }

  def createShadowStyleCombo(ctrl: ADiagramController) = {
    val cmbShadow = new ComboBox[ShadowStyleSpec]
    ctrl.styleRepository.shadowStyles.foreach(cmbShadow.getItems.add) //TODO bind this

    cmbShadow.setButtonCell(new ShadowStyleListCell)
    cmbShadow.setCellFactory(new Callback[ListView[ShadowStyleSpec], ListCell[ShadowStyleSpec]] {
      override def call(p: ListView[ShadowStyleSpec]) = new ShadowStyleListCell
    })

    cmbShadow
  }

  def createTextStyleCombo(ctrl: ADiagramController) = {
    val cmbText = new ComboBox[TextStyleSpec]

    ctrl.styleRepository.textStyles.foreach(cmbText.getItems.add) //TODO bind this

    cmbText.setButtonCell(new TextStyleListCell)
    cmbText.setCellFactory(new Callback[ListView[TextStyleSpec], ListCell[TextStyleSpec]] {
      override def call(p: ListView[TextStyleSpec]) = new TextStyleListCell
    })

    cmbText
  }
}

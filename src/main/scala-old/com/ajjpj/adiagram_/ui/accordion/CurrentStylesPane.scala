package com.ajjpj.adiagram_.ui.accordion

import com.ajjpj.adiagram_.ui.fw.Digest
import com.ajjpj.adiagram_.ui.forms.AbstractForm
import javafx.scene.control.Label
import com.ajjpj.adiagram_.model.style._
import javafx.scene.layout.Pane
import com.ajjpj.adiagram_.ui.{ADiagramController, StdControls}

/**
 * @author arno
 */
class CurrentStylesPane(ctrl: ADiagramController)(implicit digest: Digest) extends Pane {
  val selStyles = ctrl.selectedStyles

  val form = new AbstractForm {
    val cmbFill          = StdControls.createFillStyleCombo(ctrl)
    val cmbShadow        = StdControls.createShadowStyleCombo(ctrl)
    val cmbLineStyle     = StdControls.createLineStyleCombo(ctrl)
    val cmbTextStyle     = StdControls.createTextStyleCombo(ctrl)
    val cmbLineTextStyle = StdControls.createTextStyleCombo(ctrl)
    val cmbLineStart     = StdControls.createLineEndCombo(ctrl)
    val cmbLineEnd       = StdControls.createLineEndCombo(ctrl)

    bind(cmbFill.valueProperty,          selStyles.fillStyle,       (st: FillStyleSpec)   => {selStyles.fillStyle     = st})
    bind(cmbShadow.valueProperty,        selStyles.shadowStyle,     (st: ShadowStyleSpec) => {selStyles.shadowStyle   = st})
    bind(cmbLineStyle.valueProperty,     selStyles.lineStyle,       (st: LineStyleSpec)   => {selStyles.lineStyle     = st})
    bind(cmbTextStyle.valueProperty,     selStyles.textStyle,       (st: TextStyleSpec)   => {selStyles.textStyle     = st})
    bind(cmbLineTextStyle.valueProperty, selStyles.lineTextStyle,   (st: TextStyleSpec)   => {selStyles.lineTextStyle = st})
    bind(cmbLineStart.valueProperty,     selStyles.startLineEnd,    (st: LineEndSpec)     => {selStyles.startLineEnd  = st})
    bind(cmbLineEnd.valueProperty,       selStyles.endLineEnd,      (st: LineEndSpec)     => {selStyles.endLineEnd    = st})

    add(new Label("Fill Style:"),   0, 0)
    add(cmbFill, 1, 0)
    add(new Label("Shadow Style:"), 0, 1)
    add(cmbShadow, 1, 1)
    add(new Label("Line Style:"),   0, 2)
    add(cmbLineStyle, 1, 2)
    add(new Label("Text Style:"),   0, 3)
    add(cmbTextStyle, 1, 3)
    add(new Label("Line Text:"),    0, 4)
    add(cmbLineTextStyle, 1, 4)
    add(new Label("Line Start:"),   0, 5)
    add(cmbLineStart, 1, 5)
    add(new Label("Line End:"),     0, 6)
    add(cmbLineEnd, 1, 6)
  }

  getChildren.add(form)
}

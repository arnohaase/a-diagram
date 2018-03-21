package com.ajjpj.adiagram_.ui.forms

import javafx.scene.control._
import com.ajjpj.adiagram_.ui.fw.{Command, Digest}
import com.ajjpj.adiagram_.model.diagram.ABoxSpec
import com.ajjpj.adiagram_.model.style.{ShadowStyleSpec, TextStyleSpec, AStyleRepository, FillStyleSpec}
import javafx.util.Callback
import scala.Some
import com.ajjpj.adiagram_.ui.{ADiagramController, StdControls}

/**
 * @author arno
 */
class BoxDetailsForm (ctrl: ADiagramController, boxSpec: ABoxSpec)(implicit digest: Digest) extends AbstractForm {
  val styleRepository = ctrl.styleRepository

  private val txtText = new TextField()
  private val cmbFill   = StdControls.createFillStyleCombo(ctrl)
  private val cmbText   = StdControls.createTextStyleCombo(ctrl)
  private val cmbShadow = StdControls.createShadowStyleCombo(ctrl)

  add(new Label("Text:"), 0, 0)
  add(txtText, 1, 0)
  add(new Label("Fill:"), 0, 1)
  add(cmbFill, 1, 1)
  add(new Label("Text Style:"), 0, 2)
  add(cmbText, 1, 2)
  add(new Label("Shadow:"), 0, 3)
  add(cmbShadow, 1, 3)

  bind(txtText.textProperty, boxSpec.text.getOrElse(""), (txt: String) => {
    val txtOption = if (txt.trim.isEmpty) None else Some(txt)

    if(txtOption != boxSpec.text) {
      digest.undoRedo.push(new ChangeTextCommand(boxSpec, boxSpec.text, txtOption))
      boxSpec.atomicUpdate {boxSpec.text = Some(txt)}
    }
  })

  bind(cmbFill.valueProperty, boxSpec.fillStyle, (st: FillStyleSpec) => { boxSpec.fillStyle = st })
  bind(cmbText.valueProperty, boxSpec.textStyle, (st: TextStyleSpec) => { boxSpec.textStyle = st})
  bind(cmbShadow.valueProperty, boxSpec.shadowStyle, (st: ShadowStyleSpec) => { boxSpec.shadowStyle = st})

  private case class ChangeTextCommand(boxSpec: ABoxSpec, oldText: Option[String], newText: Option[String]) extends Command {
    def name = "Change Box Text"
    def isNop = oldText == newText
    def undo() {boxSpec.atomicUpdate {boxSpec.text = oldText}}
    def redo() {boxSpec.atomicUpdate {boxSpec.text = newText}}
  }
}

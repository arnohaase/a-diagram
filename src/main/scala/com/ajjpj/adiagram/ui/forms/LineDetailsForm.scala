package com.ajjpj.adiagram.ui.forms

import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import javafx.scene.control._
import com.ajjpj.adiagram.model.diagram.ALineSpec
import com.ajjpj.adiagram.model.style.{LineEndSpec, TextStyleSpec, LineStyleSpec}
import scala.Some
import com.ajjpj.adiagram.ui.{ADiagramController, StdControls}

/**
 * @author arno
 */
class LineDetailsForm (ctrl: ADiagramController, lineSpec: ALineSpec)(implicit digest: Digest) extends AbstractForm {
  val styleRepository = ctrl.styleRepository

  private val txtText = new TextField()
  private val cmbLineStyle = StdControls.createLineStyleCombo(ctrl)
  private val cmbTextStyle = StdControls.createTextStyleCombo(ctrl)
  private val cmbStart     = StdControls.createLineEndCombo(ctrl)
  private val cmbEnd       = StdControls.createLineEndCombo(ctrl)

  add(new Label("Text:"), 0, 0)
  add(txtText, 1, 0)
  add(new Label("Start:"), 0, 1)
  add(cmbStart, 1, 1)
  add(new Label("End:"), 0, 2)
  add(cmbEnd, 1, 2)
  add(new Label("Line Style:"), 0, 3)
  add(cmbLineStyle, 1, 3)
  add(new Label("Text Style:"), 0, 4)
  add(cmbTextStyle, 1, 4)

  bind(txtText.textProperty, lineSpec.text.getOrElse(""), (txt: String) => {
    val txtOption = if (txt.trim.isEmpty) None else Some(txt)

    if(txtOption != lineSpec.text) {
      digest.undoRedo.push(new ChangeTextCommand(lineSpec, lineSpec.text, txtOption))
      lineSpec.atomicUpdate {lineSpec.text = Some(txt)}
    }
  })

  bind(cmbStart.valueProperty, lineSpec.startLineEnd, (s: LineEndSpec) => { lineSpec.startLineEnd = s })
  bind(cmbEnd.  valueProperty, lineSpec.endLineEnd,   (s: LineEndSpec) => { lineSpec.endLineEnd   = s })

  bind(cmbTextStyle.valueProperty, lineSpec.textStyle, (st: TextStyleSpec) => { lineSpec.textStyle = st})
  bind(cmbLineStyle.valueProperty, lineSpec.lineStyle, (st: LineStyleSpec) => {lineSpec.lineStyle = st })



  private case class ChangeTextCommand(lineSpec: ALineSpec, oldText: Option[String], newText: Option[String]) extends Command {
    def name = "Change Line Text"
    def isNop = oldText == newText
    def undo() {lineSpec.atomicUpdate {lineSpec.text = oldText}}
    def redo() {lineSpec.atomicUpdate {lineSpec.text = newText}}
  }
}

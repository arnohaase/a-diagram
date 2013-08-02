package com.ajjpj.adiagram.ui.forms

import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import com.ajjpj.adiagram.model.{ABoxSpec, ALineSpec}
import javafx.scene.control.{Label, TextField}

/**
 * @author arno
 */
class LineDetailsForm (lineSpec: ALineSpec)(implicit digest: Digest) extends AbstractForm {
  private val txtText = new TextField()

  add(new Label("Text:"), 0, 0)
  add(txtText, 1, 0)

  bind(txtText.textProperty, lineSpec.text.getOrElse(""), (txt: String) => {
    val txtOption = if (txt.trim.isEmpty) None else Some(txt)

    if(txtOption != lineSpec.text) {
      digest.undoRedo.push(new ChangeTextCommand(lineSpec, lineSpec.text, txtOption))
      lineSpec.atomicUpdate {lineSpec.text = Some(txt)}
    }
  })

  private case class ChangeTextCommand(lineSpec: ALineSpec, oldText: Option[String], newText: Option[String]) extends Command {
    def name = "Change Line Text"
    def isNop = oldText == newText
    def undo() {lineSpec.atomicUpdate {lineSpec.text = oldText}}
    def redo() {lineSpec.atomicUpdate {lineSpec.text = newText}}
  }
}

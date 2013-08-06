package com.ajjpj.adiagram.ui.forms

import javafx.scene.control.{TextField, Label}
import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import com.ajjpj.adiagram.model.diagram.ABoxSpec

/**
 * @author arno
 */
class BoxDetailsForm (boxSpec: ABoxSpec)(implicit digest: Digest) extends AbstractForm {
  private val txtText = new TextField()

  add(new Label("Text:"), 0, 0)
  add(txtText, 1, 0)

  bind(txtText.textProperty, boxSpec.text.getOrElse(""), (txt: String) => {
    val txtOption = if (txt.trim.isEmpty) None else Some(txt)

    if(txtOption != boxSpec.text) {
      digest.undoRedo.push(new ChangeTextCommand(boxSpec, boxSpec.text, txtOption))
      boxSpec.atomicUpdate {boxSpec.text = Some(txt)}
    }
  })

  private case class ChangeTextCommand(boxSpec: ABoxSpec, oldText: Option[String], newText: Option[String]) extends Command {
    def name = "Change Box Text"
    def isNop = oldText == newText
    def undo() {boxSpec.atomicUpdate {boxSpec.text = oldText}}
    def redo() {boxSpec.atomicUpdate {boxSpec.text = newText}}
  }
}

package com.ajjpj.adiagram.ui.forms

import javafx.scene.control.{TextField, Label}
import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import com.ajjpj.adiagram.model.{ATextSpec, ABoxSpec}

/**
 * @author arno
 */
class TextDetailsForm (textSpec: ATextSpec)(implicit digest: Digest) extends AbstractForm {
  private val txtText = new TextField()


  add(new Label("Text:"), 0, 0)
  add(txtText, 1, 0)

  bind(txtText.textProperty, textSpec.text, (txt: String) => {

    if(txt != textSpec.text) {
      digest.undoRedo.push(new ChangeTextCommand(textSpec, textSpec.text, txt))
      textSpec.atomicUpdate {textSpec.text = txt}
    }
  })

  private case class ChangeTextCommand(textSpec: ATextSpec, oldText: String, newText: String) extends Command {
    def name = "Change Text"
    def isNop = oldText == newText
    def undo() {textSpec.atomicUpdate {textSpec.text = oldText}}
    def redo() {textSpec.atomicUpdate {textSpec.text = newText}}
  }
}

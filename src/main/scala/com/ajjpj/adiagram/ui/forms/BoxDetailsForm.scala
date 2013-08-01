package com.ajjpj.adiagram.ui.forms

import javafx.scene.layout.GridPane
import javafx.geometry.{Insets, Pos}
import javafx.scene.control.{TextField, Label}
import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import com.ajjpj.adiagram.model.ABoxSpec

/**
 * @author arno
 */
class BoxDetailsForm (implicit digest: Digest) extends GridPane {
  setAlignment(Pos.CENTER_LEFT) //TODO extrect to 'form' base class?
  setHgap(10)
  setVgap(10)
  setPadding(new Insets (10, 10, 10, 10))

  private val txtText = new TextField()

  add(new Label("Text:"), 0, 0)
  add(txtText, 1, 0)

  digest.registerEventSource(txtText.textProperty)

  def bind(boxSpec: ABoxSpec)(implicit digest: Digest) = {
    //TODO request focus - calling txtText.requestFocus does not appear to work...

    digest.bind(txtText.textProperty, boxSpec.text.getOrElse(""))

    digest.bind((txt: String) => { //TODO bind bidirectionally - necessary for 'undo' to work!!!
      val txtOption = if (txt.trim.isEmpty) None else Some(txt)

      if(txtOption != boxSpec.text) {
        digest.undoRedo.push(new ChangeTextCommand(boxSpec, boxSpec.text, txtOption))
        boxSpec.atomicUpdate {boxSpec.text = Some(txt)}
      }
    }, txtText.getText)
  }

  private case class ChangeTextCommand(boxSpec: ABoxSpec, oldText: Option[String], newText: Option[String]) extends Command {
    def name = "Change Box Text"

    def undo() {boxSpec.atomicUpdate {boxSpec.text = oldText}}
    def redo() {boxSpec.atomicUpdate {boxSpec.text = newText}}
  }
}

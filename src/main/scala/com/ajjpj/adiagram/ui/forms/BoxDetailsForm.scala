package com.ajjpj.adiagram.ui.forms

import javafx.scene.control.{ComboBox, TextField, Label}
import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import com.ajjpj.adiagram.model.diagram.ABoxSpec
import com.ajjpj.adiagram.model.style.{AStyleRepository, FillStyleSpec}
import javafx.beans.property.Property

/**
 * @author arno
 */
class BoxDetailsForm (styleRepository: AStyleRepository, boxSpec: ABoxSpec)(implicit digest: Digest) extends AbstractForm {
  private val txtText = new TextField()
  private val cmbFill = new ComboBox[FillStyleSpec]()

  add(new Label("Text:"), 0, 0)
  add(txtText, 1, 0)
  add(new Label("Fill:"), 0, 1)
  add(cmbFill, 1, 1)

  bind(txtText.textProperty, boxSpec.text.getOrElse(""), (txt: String) => {
    val txtOption = if (txt.trim.isEmpty) None else Some(txt)

    if(txtOption != boxSpec.text) {
      digest.undoRedo.push(new ChangeTextCommand(boxSpec, boxSpec.text, txtOption))
      boxSpec.atomicUpdate {boxSpec.text = Some(txt)}
    }
  })

  styleRepository.fillStyles.foreach(cmbFill.getItems.add) //TODO bind this --> changes to the repo while the dialog is initialized
  bind(cmbFill.valueProperty, boxSpec.fillStyle, (st: FillStyleSpec) => { boxSpec.fillStyle = st })

  private case class ChangeTextCommand(boxSpec: ABoxSpec, oldText: Option[String], newText: Option[String]) extends Command {
    def name = "Change Box Text"
    def isNop = oldText == newText
    def undo() {boxSpec.atomicUpdate {boxSpec.text = oldText}}
    def redo() {boxSpec.atomicUpdate {boxSpec.text = newText}}
  }
}

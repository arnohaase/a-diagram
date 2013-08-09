package com.ajjpj.adiagram.ui.forms

import javafx.scene.control._
import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import com.ajjpj.adiagram.model.diagram.ATextSpec
import com.ajjpj.adiagram.model.style.{AStyleRepository, TextStyleSpec}
import javafx.util.Callback

/**
 * @author arno
 */
class TextDetailsForm (styleRepository: AStyleRepository, textSpec: ATextSpec)(implicit digest: Digest) extends AbstractForm {
  private val txtText = new TextField()
  private val cmbText = new ComboBox[TextStyleSpec]

  add(new Label("Text:"), 0, 0)
  add(txtText, 1, 0)
  add(new Label("Style:"), 0, 1)
  add(cmbText, 1, 1)

  bind(txtText.textProperty, textSpec.text, (txt: String) => {
    if(txt != textSpec.text) {
      digest.undoRedo.push(new ChangeTextCommand(textSpec, textSpec.text, txt))
      textSpec.atomicUpdate {textSpec.text = txt}
    }
  })

  styleRepository.textStyles.foreach(cmbText.getItems.add) //TODO bind this
  bind(cmbText.valueProperty, textSpec.textStyle, (st: TextStyleSpec) => { textSpec.textStyle = st})

  cmbText.setButtonCell(new TextStyleListCell)
  cmbText.setCellFactory(new Callback[ListView[TextStyleSpec], ListCell[TextStyleSpec]] {
    override def call(p: ListView[TextStyleSpec]) = new TextStyleListCell
  })


  private case class ChangeTextCommand(textSpec: ATextSpec, oldText: String, newText: String) extends Command {
    def name = "Change Text"
    def isNop = oldText == newText
    def undo() {textSpec.atomicUpdate {textSpec.text = oldText}}
    def redo() {textSpec.atomicUpdate {textSpec.text = newText}}
  }
}

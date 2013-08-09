package com.ajjpj.adiagram.ui.forms

import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import javafx.scene.control._
import com.ajjpj.adiagram.model.diagram.ALineSpec
import com.ajjpj.adiagram.model.style.{TextStyleSpec, LineStyleSpec, AStyleRepository}
import javafx.util.Callback
import scala.Some

/**
 * @author arno
 */
class LineDetailsForm (styleRepository: AStyleRepository, lineSpec: ALineSpec)(implicit digest: Digest) extends AbstractForm {
  private val txtText = new TextField()
  private val cmbLineStyle = new ComboBox[LineStyleSpec]
  private val cmbTextStyle = new ComboBox[TextStyleSpec]

  add(new Label("Text:"), 0, 0)
  add(txtText, 1, 0)
  add(new Label("Line Style:"), 0, 1)
  add(cmbLineStyle, 1, 1)
  add(new Label("Text Style:"), 0, 2)
  add(cmbTextStyle, 1, 2)

  bind(txtText.textProperty, lineSpec.text.getOrElse(""), (txt: String) => {
    val txtOption = if (txt.trim.isEmpty) None else Some(txt)

    if(txtOption != lineSpec.text) {
      digest.undoRedo.push(new ChangeTextCommand(lineSpec, lineSpec.text, txtOption))
      lineSpec.atomicUpdate {lineSpec.text = Some(txt)}
    }
  })

  styleRepository.textStyles.foreach(cmbTextStyle.getItems.add) //TODO bind this
  bind(cmbTextStyle.valueProperty, lineSpec.textStyle, (st: TextStyleSpec) => { lineSpec.textStyle = st})

  cmbTextStyle.setButtonCell(new TextStyleListCell)
  cmbTextStyle.setCellFactory(new Callback[ListView[TextStyleSpec], ListCell[TextStyleSpec]] {
    override def call(p: ListView[TextStyleSpec]) = new TextStyleListCell
  })

  styleRepository.lineStyles.foreach(cmbLineStyle.getItems.add)
  bind(cmbLineStyle.valueProperty, lineSpec.lineStyle, (st: LineStyleSpec) => {lineSpec.lineStyle = st })

  cmbLineStyle.setButtonCell(new LineStyleListCell)
  cmbLineStyle.setCellFactory(new Callback[ListView[LineStyleSpec], ListCell[LineStyleSpec]] {
    def call(p1: ListView[LineStyleSpec]) = new LineStyleListCell
  })


  private case class ChangeTextCommand(lineSpec: ALineSpec, oldText: Option[String], newText: Option[String]) extends Command {
    def name = "Change Line Text"
    def isNop = oldText == newText
    def undo() {lineSpec.atomicUpdate {lineSpec.text = oldText}}
    def redo() {lineSpec.atomicUpdate {lineSpec.text = newText}}
  }
}

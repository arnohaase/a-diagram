package com.ajjpj.adiagram.ui.forms

import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import javafx.scene.control._
import com.ajjpj.adiagram.model.diagram.ALineSpec
import com.ajjpj.adiagram.model.style.{LineEndSpec, TextStyleSpec, LineStyleSpec, AStyleRepository}
import javafx.util.Callback
import scala.Some

/**
 * @author arno
 */
class LineDetailsForm (styleRepository: AStyleRepository, lineSpec: ALineSpec)(implicit digest: Digest) extends AbstractForm {
  private val txtText = new TextField()
  private val cmbLineStyle = new ComboBox[LineStyleSpec]
  private val cmbTextStyle = new ComboBox[TextStyleSpec]
  private val cmbStart = new ComboBox[LineEndSpec]
  private val cmbEnd = new ComboBox[LineEndSpec]

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

  styleRepository.lineEnds.foreach(cmbStart.getItems.add)
  styleRepository.lineEnds.foreach(cmbEnd.  getItems.add)
  bind(cmbStart.valueProperty, lineSpec.startLineEnd, (s: LineEndSpec) => { lineSpec.startLineEnd = s })
  bind(cmbEnd.  valueProperty, lineSpec.endLineEnd,   (s: LineEndSpec) => { lineSpec.endLineEnd   = s })

  cmbStart.setButtonCell(new LineEndListCell)
  cmbEnd.setButtonCell(new LineEndListCell)

  cmbStart.setCellFactory(new Callback[ListView[LineEndSpec], ListCell[LineEndSpec]] {
    def call(p1: ListView[LineEndSpec]) = new LineEndListCell
  })
  cmbEnd.setCellFactory(new Callback[ListView[LineEndSpec], ListCell[LineEndSpec]] {
    def call(p1: ListView[LineEndSpec]) = new LineEndListCell
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

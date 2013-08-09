package com.ajjpj.adiagram.ui.forms

import javafx.scene.control._
import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import com.ajjpj.adiagram.model.diagram.ABoxSpec
import com.ajjpj.adiagram.model.style.{ShadowStyleSpec, TextStyleSpec, AStyleRepository, FillStyleSpec}
import javafx.util.Callback
import scala.Some

/**
 * @author arno
 */
class BoxDetailsForm (styleRepository: AStyleRepository, boxSpec: ABoxSpec)(implicit digest: Digest) extends AbstractForm {
  private val txtText = new TextField()
  private val cmbFill = new ComboBox[FillStyleSpec]()
  private val cmbText = new ComboBox[TextStyleSpec]()
  private val cmbShadow = new ComboBox[ShadowStyleSpec]()

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

  styleRepository.fillStyles.foreach(cmbFill.getItems.add) //TODO bind this --> changes to the repo while the dialog is initialized
  bind(cmbFill.valueProperty, boxSpec.fillStyle, (st: FillStyleSpec) => { boxSpec.fillStyle = st })

  cmbFill.setButtonCell(new FillStyleListCell)
  cmbFill.setCellFactory(new Callback[ListView[FillStyleSpec], ListCell[FillStyleSpec]] {
    override def call(p1: ListView[FillStyleSpec]) = new FillStyleListCell
  })

  styleRepository.textStyles.foreach(cmbText.getItems.add) //TODO bind this
  bind(cmbText.valueProperty, boxSpec.textStyle, (st: TextStyleSpec) => { boxSpec.textStyle = st})

  cmbText.setButtonCell(new TextStyleListCell)
  cmbText.setCellFactory(new Callback[ListView[TextStyleSpec], ListCell[TextStyleSpec]] {
    override def call(p: ListView[TextStyleSpec]) = new TextStyleListCell
  })

  styleRepository.shadowStyles.foreach(cmbShadow.getItems.add) //TODO bind this
  bind(cmbShadow.valueProperty, boxSpec.shadowStyle, (st: ShadowStyleSpec) => { boxSpec.shadowStyle = st})

  cmbShadow.setButtonCell(new ShadowStyleListCell)
  cmbShadow.setCellFactory(new Callback[ListView[ShadowStyleSpec], ListCell[ShadowStyleSpec]] {
    override def call(p: ListView[ShadowStyleSpec]) = new ShadowStyleListCell
  })

  private case class ChangeTextCommand(boxSpec: ABoxSpec, oldText: Option[String], newText: Option[String]) extends Command {
    def name = "Change Box Text"
    def isNop = oldText == newText
    def undo() {boxSpec.atomicUpdate {boxSpec.text = oldText}}
    def redo() {boxSpec.atomicUpdate {boxSpec.text = newText}}
  }
}

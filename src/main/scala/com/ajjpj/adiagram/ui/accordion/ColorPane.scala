package com.ajjpj.adiagram.ui.accordion

import javafx.scene.layout.{VBox, Pane}
import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import com.ajjpj.adiagram.ui.{ColorListCell, StyleListCellFactory, ADiagramController}
import javafx.scene.control._
import com.ajjpj.adiagram.model.style.ColorSpec
import com.ajjpj.adiagram.ui.forms.AbstractForm
import scala.Some
import javafx.scene.paint.Color
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.event.ActionEvent


/**
 * @author arno
 */
class ColorPane(ctrl: ADiagramController)(implicit digest: Digest) extends Pane {
  private var selectedColor: Option[ColorSpec] = None

  val content = new VBox(8)
  getChildren.add(content)

  val form = new AbstractForm() {
    def updateColor(color: Color) {
      if(selectedColor.isDefined) {
        val colSpec = selectedColor.get
        if(colSpec.color != color) {
          digest.undoRedo.push(new ChangeColorCommand(colSpec, colSpec.name, colSpec.name, colSpec.color, color))
          colSpec.color = color
          ctrl.styleRepository.changeCounter += 1
        }
      }
    }
    def updateName(name: String) {
      if(selectedColor.isDefined) {
        val colSpec = selectedColor.get
        if(colSpec.name != name) {
          digest.undoRedo.push(new ChangeColorCommand(colSpec, colSpec.name, name, colSpec.color, colSpec.color))
          colSpec.name = name
          ctrl.styleRepository.changeCounter += 1
        }
      }
    }

    class ChangeColorCommand(colorSpec: ColorSpec, oldName: String, newName: String, oldColor: Color, newColor: Color) extends Command {
      def name = "Change Color"
      def isNop = false

      def undo() {
        colorSpec.name = oldName
        colorSpec.color = oldColor
        ctrl.styleRepository.changeCounter += 1
      }
      def redo() {
        colorSpec.name = newName
        colorSpec.color = newColor
        ctrl.styleRepository.changeCounter += 1
      }
    }

    val rectColor = new ColorPicker

    rectColor.getStyleClass.add(ColorPicker.STYLE_CLASS_BUTTON)
    //TODO the 'fireEvent' part is a workaround for a bug that should be fixed in JavaFX 8
    digest.bind((c: Color) => {rectColor.setValue(c); rectColor.fireEvent(new ActionEvent())}, selectedColor.map(_.color).getOrElse(Color.TRANSPARENT))
    digest.bind(updateColor _, rectColor.valueProperty)
    val txtName = new TextField()
    digest.bind(txtName.textProperty, selectedColor.map(_.name).getOrElse(""))
    digest.bind(updateName _, txtName.textProperty)

    digest.bindBoolean(rectColor.disableProperty, selectedColor.isEmpty)
    digest.bindBoolean(txtName.  disableProperty, selectedColor.isEmpty)

    add(new Label("Color:"), 0, 0)
    add(rectColor, 1, 0)
    add(new Label("Name:"), 0, 1)
    add(txtName, 1, 1)
  }
  content.getChildren.add(form)

  val list = new ListView[ColorSpec]
  list.setCellFactory(StyleListCellFactory[ColorSpec, ColorListCell])
  content.getChildren.add(list)

  val changeListener = new ChangeListener[ColorSpec] {
    def changed(observable: ObservableValue[_ <: ColorSpec], oldValue: ColorSpec, newValue: ColorSpec) {
      digest.execute {
        if(newValue == null)
          selectedColor = None
        else
          selectedColor = Some(newValue)
      }
    }
  }

  digest.watch(ctrl.styleRepository.changeCounter, refresh _)
  refresh()

  def refresh() {
    list.getSelectionModel.selectedItemProperty.removeListener(changeListener)

    selectedColor match {
      case Some(c) if ! ctrl.styleRepository.colors.contains(c) => selectedColor = None
      case _ =>
    }

    list.getItems.clear()

    ctrl.styleRepository.colors.foreach(c => {
      list.getItems.add(c)
    })

    selectedColor match {
      case Some(c) => list.getSelectionModel.select(c)
      case None =>
    }

    list.getSelectionModel.selectedItemProperty.addListener(changeListener)
  }
}

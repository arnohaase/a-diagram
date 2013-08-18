package com.ajjpj.adiagram.ui.accordion

import com.ajjpj.adiagram.ui.{StyleListCellFactory, ADiagramController}
import com.ajjpj.adiagram.ui.fw.{Command, Digest}
import javafx.scene.layout.{VBox, Pane}
import javafx.scene.control._
import javafx.beans.value.{ObservableValue, ChangeListener}
import scala.reflect.ClassTag
import com.ajjpj.adiagram.ui.forms.AbstractForm
import javafx.scene.paint.Color
import javafx.event.ActionEvent
import scala.Some
import javafx.collections.FXCollections
import scala.collection.JavaConversions

/**
 * @author arno
 */
private[accordion] abstract class AbstractStylePane[S, C <: ListCell[S], Cmd <: Command] (ctrl: ADiagramController)(implicit digest: Digest, cellClass: ClassTag[C]) extends Pane {
  def all: Seq[S]
  def snapshot: Cmd

  protected var selected: Option[S] = None

  protected val form = new AbstractForm {}
  private var formRow = 0

  protected def combo[T](label: String, values: => Seq[T], getter: => T, setter: (Cmd, T) => Cmd) = {
    val lbl = new Label(label)
    val cmb = new ComboBox[T]()
    digest.bind(cmb.itemsProperty, FXCollections.observableArrayList (JavaConversions.asJavaCollection (values)))

    digest.bind(cmb.valueProperty, getter)
    digest.bind(updateProp(getter, setter), cmb.valueProperty)

    digest.bindBoolean(cmb.disableProperty, selected.isEmpty)

    form.add(lbl, 0, formRow)
    form.add(cmb, 1, formRow)

    formRow += 1
    (lbl, cmb)
  }

  protected def textfield(label: String, getter: => String, setter: (Cmd, String) => Cmd) = {
    val lbl = new Label(label)
    val txt = new TextField
    digest.bind(txt.textProperty, getter)
    digest.bind(updateProp(getter, setter), txt.textProperty)

    digest.bindBoolean(txt.disableProperty, selected.isEmpty)

    form.add(lbl, 0, formRow)
    form.add(txt, 1, formRow)

    formRow += 1
    (lbl, txt)
  }

  protected def color(label: String, getter: => Color, setter: (Cmd, Color) => Cmd) = {
    val lbl = new Label(label)
    val rectColor = new ColorPicker

    rectColor.getStyleClass.add(ColorPicker.STYLE_CLASS_BUTTON)
    //TODO the 'fireEvent' part is a workaround for a bug that should be fixed in JavaFX 8
    digest.bind((c: Color) => {rectColor.setValue(c); rectColor.fireEvent(new ActionEvent())}, getter)
    digest.bind(updateProp(getter, setter), rectColor.valueProperty)

    digest.bindBoolean(rectColor.disableProperty, selected.isEmpty)

    form.add(lbl, 0, formRow)
    form.add(rectColor, 1, formRow)

    formRow += 1
    (lbl, rectColor)
  }


  val content = new VBox(8)
  getChildren.add(content)

  val list = new ListView[S]
  list.setCellFactory(StyleListCellFactory[S, C])

  content.getChildren.addAll(form, list)

  protected def updateProp[T](oldValue: => T, cmdWithNewValue: (Cmd, T) => Cmd) = (newValue: T) => {
    if(selected.isDefined) {
      if(oldValue != newValue) {
        val cmd = cmdWithNewValue(snapshot, newValue)
        digest.undoRedo.push(cmd)
        cmd.redo()
        ctrl.styleRepository.changeCounter += 1
      }
    }
  }


  val changeListener = new ChangeListener[S] {
    def changed(observable: ObservableValue[_ <: S], oldValue: S, newValue: S) {
      digest.execute {
        if(newValue == null)
          selected = None
        else
          selected = Some(newValue)
      }
    }
  }

  digest.watch(ctrl.styleRepository.changeCounter, () => {onStyleRepoChanged(); refresh()})
  refresh()

  protected def onStyleRepoChanged() {}

  def refresh() {
    list.getSelectionModel.selectedItemProperty.removeListener(changeListener)

    selected match {
      case Some(c) if ! all.contains(c) => selected = None
      case _ =>
    }

    list.getItems.clear()

    all.foreach(c => {
      list.getItems.add(c)
    })

    selected match {
      case Some(c) => list.getSelectionModel.select(c)
      case None =>
    }

    list.getSelectionModel.selectedItemProperty.addListener(changeListener)
  }
}

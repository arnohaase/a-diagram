package com.ajjpj.adiagram.ui.forms

import com.ajjpj.adiagram.model.style.{ShadowStyleSpec, LineStyleSpec, TextStyleSpec, FillStyleSpec}
import javafx.scene.control.{Label, ListCell}
import javafx.scene.shape.Rectangle
import javafx.scene.layout.FlowPane
import javafx.scene.paint.Color


/**
 * @author arno
 */
class FillStyleListCell extends ListCell[FillStyleSpec] {
  val pane = new FlowPane
  val rect = new Rectangle(25, 15)
  rect.setStroke(Color.BLACK)
  val label = new Label
  pane.setHgap(5)
  pane.getChildren.addAll(rect, label)

  override def updateItem(item: FillStyleSpec, empty: Boolean) {
    super.updateItem(item, empty)

    if(item == null) {
      setGraphic(null)
    }
    else {
      rect.setFill(item.paint)
      label.setText(item.name)
      setGraphic(pane)
    }
  }
}

class TextStyleListCell extends ListCell[TextStyleSpec] {
  override def updateItem(item: TextStyleSpec, empty: Boolean) {
    super.updateItem(item, empty)

    if(item == null) {
      setText(null)
    }
    else {
      setText(item.name)
    }
  }
}

class LineStyleListCell extends ListCell[LineStyleSpec] {
  override def updateItem(item: LineStyleSpec, empty: Boolean) {
    super.updateItem(item, empty)

    if(item == null) {
      setText(null)
    }
    else {
      setText(item.name)
    }
  }
}

class ShadowStyleListCell extends ListCell[ShadowStyleSpec] {
  override def updateItem(item: ShadowStyleSpec, empty: Boolean) {
    super.updateItem(item, empty)

    if(item == null) {
      setText(null)
    }
    else {
      setText(item.name)
    }
  }
}


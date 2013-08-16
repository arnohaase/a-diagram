package com.ajjpj.adiagram.ui.forms

import com.ajjpj.adiagram.model.style._
import javafx.scene.control.{Label, ListCell}
import javafx.scene.shape.Rectangle
import javafx.scene.layout.{HBox, FlowPane}
import javafx.scene.paint.Color


/**
 * @author arno
 */
class FillStyleListCell extends ListCell[FillStyleSpec] {
  val pane = new HBox(5)
  val rect = new Rectangle(25, 15)
  rect.setStroke(Color.BLACK)
  val label = new Label
  label.setTextFill(Color.BLACK)
//  pane.setHgap(5)
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

class LineEndListCell extends ListCell[LineEndSpec] {
  override def updateItem(item: LineEndSpec, empty: Boolean) {
    super.updateItem(item, empty)

    if(item == null) {
      setText(null)
    }
    else {
      setText(item.name) //TODO visualize the line end
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


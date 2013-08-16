package com.ajjpj.adiagram.ui

import com.ajjpj.adiagram.model.style._
import javafx.scene.control.{IndexedCell, TreeCell, Label, ListCell}
import javafx.scene.shape.Rectangle
import javafx.scene.layout.{HBox, FlowPane}
import javafx.scene.paint.{Paint, Color}


/**
 * @author arno
 */
class FillStyleListCell   extends RectAndTextListCell[FillStyleSpec] (item => item.paint, item => item.name)
class TextStyleListCell   extends TextListCell[TextStyleSpec] (item => item.name)
class LineStyleListCell   extends TextListCell[LineStyleSpec] (item => item.name)
class LineEndListCell     extends TextListCell[LineEndSpec] (item => item.name) //TODO visualize line end
class ShadowStyleListCell extends TextListCell[ShadowStyleSpec] (item => item.name)

class ColorTreeCell     extends RectAndTextTreeCell[ColorSpec] (item => item.color, item => item.name)
class FillStyleTreeCell extends RectAndTextTreeCell[FillStyleSpec] (item => item.paint, item => item.name)

//--------------------------------------

class RectAndTextTreeCell[T](val fill: T => Paint, val text: T => String) extends TreeCell[T] with RectAndTextIndexedCell[T]
class RectAndTextListCell[T](val fill: T => Paint, val text: T => String) extends ListCell[T] with RectAndTextIndexedCell[T]

class TextListCell[T](val text: T => String) extends ListCell[T] with TextIndexedCell[T]
class TextTreeCell[T](val text: T => String) extends ListCell[T] with TextIndexedCell[T]

//--------------------------------------

trait RectAndTextIndexedCell[T] extends IndexedCell[T] {
  def fill: T => Paint
  def text: T => String

  val content = new HBox(5)
  val rect = new Rectangle(25,15)
  rect.setStroke(Color.BLACK)

  val label = new Label
  label.setTextFill(Color.BLACK)

  content.getChildren.addAll(rect, label)

  override def updateItem(item: T, empty: Boolean) {
    super.updateItem(item, empty)

    if(item == null) {
      setGraphic(null)
    }
    else {
      rect.setFill (fill (item))
      label.setText (text (item))
      setGraphic(content)
    }
  }
}

trait TextIndexedCell[T] extends IndexedCell[T] {
  def text: T => String

  override def updateItem(item: T, empty: Boolean) {
    super.updateItem(item, empty)

    if(item == null) {
      setText(null)
    }
    else {
      setText(text(item))
    }
  }
}




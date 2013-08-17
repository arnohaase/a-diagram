package com.ajjpj.adiagram.ui

import com.ajjpj.adiagram.model.style._
import javafx.scene.control._
import javafx.scene.shape.Rectangle
import javafx.scene.layout.HBox
import javafx.scene.paint.{Paint, Color}
import javafx.util.Callback
import scala.reflect.ClassTag


/**
 * @author arno
 */
class ColorListCell       extends RectAndTextListCell[ColorSpec] (item => item.color, item => item.name)
class FillStyleListCell   extends RectAndTextListCell[FillStyleSpec] (item => item.paint, item => item.name)
class TextStyleListCell   extends TextListCell[TextStyleSpec] (item => item.name)
class LineStyleListCell   extends TextListCell[LineStyleSpec] (item => item.name)
class LineEndListCell     extends TextListCell[LineEndSpec] (item => item.name) //TODO visualize line end
class ShadowStyleListCell extends TextListCell[ShadowStyleSpec] (item => item.name)

//--------------------------------------

object StyleListCellFactory {
  def apply[S, T <: ListCell[S]](implicit cls: ClassTag[T]) = new Callback[ListView[S], ListCell[S]] {
    override def call(p1: ListView[S]) = cls.runtimeClass.newInstance.asInstanceOf[T]
  }
}

//--------------------------------------

class RectAndTextTreeCell[T](val fill: T => Paint, val text: T => String) extends TreeCell[T] with RectAndTextIndexedCell[T]
class RectAndTextListCell[T](val fill: T => Paint, val text: T => String) extends ListCell[T] with RectAndTextIndexedCell[T]

class TextListCell[T](val text: T => String) extends ListCell[T] with TextIndexedCell[T]
class TextTreeCell[T](val text: T => String) extends ListCell[T] with TextIndexedCell[T]

//--------------------------------------

object StyleTreeCellFactory extends Callback[TreeView[AnyRef], TreeCell[AnyRef]] {
  def call(p1: TreeView[AnyRef]): TreeCell[AnyRef] = {
    new TreeCell[AnyRef] {
      val colorCell = new ColorListCell()
      val fillStyleCell = new FillStyleListCell()
      val textStyleCell = new TextStyleListCell()
      val lineStyleCell = new LineStyleListCell()

      override def updateItem(item: AnyRef, empty: Boolean) {
        super.updateItem(item, empty)

        item match {
          case i: ColorSpec =>
            colorCell.updateItem(i, empty)
            setGraphic(colorCell.content)
          case i: FillStyleSpec =>
            fillStyleCell.updateItem(i, empty)
            setGraphic(fillStyleCell.content)
          case i: TextStyleSpec =>
            textStyleCell.updateItem(i, empty) //TODO graphic
            setText(textStyleCell.getText)
          case i: LineStyleSpec =>
            lineStyleCell.updateItem(i, empty) //TODO graphic
            setText(lineStyleCell.getText)
          case _ =>
            setGraphic(null)
        }
      }
    }
  }
}

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




package com.ajjpj.adiagram.ui.forms

import javafx.scene.layout.GridPane
import com.ajjpj.adiagram.ui.fw.{Digest, Unbindable}
import javafx.geometry.{Insets, Pos}
import javafx.beans.property.Property

/**
 * @author arno
 */
abstract class AbstractForm(implicit digest: Digest) extends GridPane with Unbindable {
  type SetterType = (_) => Unit

  private var boundProps   = List[Property[_]]()
  private var boundSetters = List[SetterType]()

  setAlignment(Pos.CENTER_LEFT)
  setHgap(10)
  setVgap(10)
  setPadding(new Insets (10, 10, 10, 10))

  def bind[T] (prop: Property[T], getter: => T, setter: T => Unit): Unit = {
    digest.registerEventSource(prop)

    digest.bind(prop, getter)
    digest.bind(setter, prop.getValue)

    boundProps = prop :: boundProps
    boundSetters = setter.asInstanceOf[SetterType] :: boundSetters
  }

  def bind[T] (getter1: => T, setter1: T => Unit, getter2: => T, setter2: T => Unit): Unit = {
    digest.bind(setter1, getter2)
    digest.bind(setter2, getter1)

    boundSetters = setter1.asInstanceOf[SetterType] :: setter2.asInstanceOf[SetterType] :: boundSetters
  }

  override def unbind()(implicit digest: Digest) {
    boundProps.foreach(digest.unbind)
    boundSetters.foreach(x => digest.unbind (x))

    boundProps = Nil
    boundSetters = Nil
  }
}

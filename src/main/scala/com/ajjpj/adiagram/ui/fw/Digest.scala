package com.ajjpj.adiagram.ui.fw

import javafx.beans.property.Property
import scala.collection.mutable
import javafx.event.{EventHandler, Event}
import scala.language.implicitConversions


/**
 * All (UI related) application code should be executed as a callback through this class, either directly
 *  or indirectly. It takes care of infrastructure stuff like binding and error handling.
 *
 * @author arno
 */
class Digest() {
  val undoRedo = new UndoRedoStack

  private val bindings = new Bindings()
  private var postprocessors: List[() => Unit] = List(refreshAllBindings _)

  def registerPostprocessor(p: () => Unit) = postprocessors = p :: postprocessors

  def execute[T](callback: => T): Option[T] = {
    try {
      Some(callback)
    }
    catch {
      case exc: Exception => handleCaughtException(exc)
    }
    finally {
      try {
         postprocessors.foreach((p: () => Unit) => p())
      }
      catch {
        case exc: Throwable => handleCaughtException(exc)
      }
    }
  }

  private def refreshAllBindings() {
    bindings.refreshAll()
  }

  def handleCaughtException(exc: Throwable) = {
    exc.printStackTrace() //TODO exception handling
    None
  }

  //TODO bidirectional bindings
  def bind[T](property: Property[T], expression: => T)                        = bindings.bind(property, () => expression)
  def bindBoolean                      (property: Property[java.lang.Boolean], expression: => Boolean) = bindings.bind(property, () => expression.asInstanceOf[java.lang.Boolean])
  def bindDouble[T <: java.lang.Number](property: Property[T],                 expression: => Double)  = bindings.bind(property, () => expression.asInstanceOf[T])
  def unbind(property: Property[_]) = bindings.unbind(property)

  implicit def createEventHandler[T <: Event, R] (handler: T => R) = new EventHandler[T]() {
    def handle(p1: T) = execute (handler(p1))
  }

  private[Digest] class Binding[T] (property: Property[T], expression: () => T) {
    def refresh() = property.setValue(expression())
  }

  private class Bindings {
    private val bindings = new mutable.WeakHashMap[Property[_], Binding[_]] ()
    //TODO warning if a weak reference actually gets collected?!

    def bind[T](property: Property[T], expression: ()=>T) {
      val binding = new Binding(property, expression)
      bindings += ((property, binding))
      binding.refresh()
    }

    def unbind(property: Property[_]) = bindings -= property
    def refreshAll() = bindings.values.foreach(_.refresh())
  }
}


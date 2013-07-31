package com.ajjpj.adiagram.ui.fw

import javafx.beans.property.Property
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

  //TODO bidirectional bindings (?)
  def bind[T] (target: T => _, source: => T) = bindings.bind(target, () => source)
  def unbind[T] (target: T => _) = bindings.unbind(target)

  def bind[T]                          (property: Property[T],                 expression: => T)       = bindings.bind(PropertyTarget(property), () => expression)
  def bindBoolean                      (property: Property[java.lang.Boolean], expression: => Boolean) = bindings.bind(PropertyTarget(property), () => expression.asInstanceOf[java.lang.Boolean])
  def bindDouble[T <: java.lang.Number](property: Property[T],                 expression: => Double)  = bindings.bind(PropertyTarget(property), () => expression.asInstanceOf[T])
  def unbind(property: Property[_]) = bindings.unbind(PropertyTarget(property))
  def isBound(property: Property[_]) = bindings.isBound(PropertyTarget(property))

  implicit def createEventHandler[T <: Event, R] (handler: T => R) = new EventHandler[T]() {
    def handle(p1: T) = execute (handler(p1))
  }

  private[Digest] case class Binding[T] (target: T => _, source: () => T) {
    def eval = Eval[T] (this, source())
  }
  private[Digest] case class Eval[T] (binding: Binding[T], value: T) {
    def update() = binding.target(value)
  }

  private[Digest] case class PropertyTarget[T] (prop: Property[T]) extends Function1[T, Unit] {
    override def apply(value: T) = prop.setValue(value)
  }

  private class Bindings {
    private var bindings = Map[Function1[_, _], Binding[_]] ()

    def bind[T](target: T => _, source: ()=>T) {
      val binding = new Binding(target, source)
      bindings += ((target, binding))
      binding.eval.update()
    }

    def isBound[T] (target: T => _) = bindings contains target

    def unbind[T] (target: T => _):    Unit = if(isBound(target)) bindings -= target  else throw new IllegalArgumentException("target not bound")

    def refreshAll() = {
      var iterThreshold = SystemConfiguration.maxBindingRefreshIterations

      def eval = bindings.values.map(_.eval)

      var prevValues: Traversable[Eval[_]] = Nil
      var newValues = eval

      while(newValues != prevValues) {
        newValues.foreach(_.update()) //TODO is it more efficient to only update those values that changed since last time?
        prevValues = newValues
        newValues = eval

        iterThreshold -= 1
        if(iterThreshold < 0) {
          throw new IllegalStateException("binding values do not converge - terminating")
        }
      }
    }
  }
}


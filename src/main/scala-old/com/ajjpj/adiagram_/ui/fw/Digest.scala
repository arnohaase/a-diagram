package com.ajjpj.adiagram_.ui.fw

import javafx.beans.property.Property
import javafx.event.{EventHandler, Event}
import scala.language.implicitConversions
import javafx.beans.value.{ObservableValue, ChangeListener}


/**
 * All (UI related) application code should be executed as a callback through this class, either directly
 *  or indirectly. It takes care of infrastructure stuff like binding and error handling.
 *
 * @author arno
 */
class Digest() {
  val undoRedo = new UndoRedoStack

  private val bindings = new Bindings()
  private var postprocessors: List[() => Unit] = Nil

  private var _isExecuting = false
  def isExecuting = _isExecuting

  def registerPostprocessor(p: () => Unit) = postprocessors = p :: postprocessors

  def execute[T](callback: => T): Option[T] = {
    if(isExecuting)
      Some(callback)
    else {
      _isExecuting = true

      val initialSnapshot = bindings.snapshot
      try {
        Some(callback)
      }
      catch {
        case exc: Exception => handleCaughtException(exc)
      }
      finally {
        try {
          bindings.refreshAll(initialSnapshot)
          postprocessors.foreach((p: () => Unit) => p())
        }
        catch {
          case exc: Throwable => handleCaughtException(exc)
        }
        _isExecuting = false
      }
    }
  }

  def handleCaughtException(exc: Throwable) = {
    exc.printStackTrace() //TODO exception handling
    None
  }

  /**
   * This causes changes to a JavaFX property to be fired through the Digest - i.e. even if a change to
   *  the property happens *outside* an 'execute' call, that change triggers a Digest loop
   */
  def registerEventSource[T] (prop: Property[T]) {
    prop.addListener(new ChangeListener[T] {
      var propChangeInProgress = false

      def changed(o: ObservableValue[_ <: T], oldValue: T, newValue: T) {
        if(! propChangeInProgress && !isExecuting) {
          propChangeInProgress = true
          try {
            prop.setValue(oldValue)
            execute {
              prop.setValue(newValue)
            }
          }
          finally {
            propChangeInProgress = false
          }
        }
      }
    })
  }

  def watch[T] (value: => T, callback: () => _): Unit = watch(value, (_: T) => callback())
  def watch[T] (value: => T, callback: T => _):  Unit = bindings.bind(callback, () => value, updateInitially=false)

  def bind[T]   (target: T => _, source: Property[T]): Unit = {registerEventSource(source); bind(target, source.getValue)}
  def bind[T]   (target: T => _, source: => T) = bindings.bind(target, () => source)
  def unbind[T] (target: T => _)               = bindings.unbind(target)

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
    type BindingKey = Function1[_,_]

    private var bindings = Map[BindingKey, Binding[_]] ()

    def bind[T](target: T => _, source: ()=>T, updateInitially: Boolean = true) {
      val binding = new Binding(target, source)
      bindings += ((target, binding))

      if(updateInitially)
        binding.eval.update()
    }

    def isBound[T] (target: T => _) = bindings contains target

    def unbind[T] (target: T => _): Unit = if(isBound(target)) bindings -= target else throw new IllegalArgumentException("target not bound")

    def snapshot: Map[BindingKey, Eval[_]] = bindings.transform((k, v) => v.eval)

    def refreshAll(initialSnapshot: Map[BindingKey, Eval[_]]) = {
      var iterThreshold = SystemConfiguration.maxBindingRefreshIterations

      var prevValues = initialSnapshot
      var newValues = snapshot

      while(newValues != prevValues) {
        val changedFilter = (e: (BindingKey, Eval[_])) => e._2.value != prevValues.get(e._1).map(_.value).getOrElse(this)

        newValues.withFilter(changedFilter).foreach(_._2.update())
        prevValues = newValues
        newValues = snapshot

        iterThreshold -= 1
        if(iterThreshold < 0) {
          throw new IllegalStateException("binding values do not converge - terminating")
        }
      }
    }
  }
}


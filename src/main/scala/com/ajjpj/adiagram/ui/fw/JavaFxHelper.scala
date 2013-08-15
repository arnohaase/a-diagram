package com.ajjpj.adiagram.ui.fw

import javafx.stage.{Window, Modality, StageStyle, Stage}
import javafx.scene.{Scene, Node}
import java.util.concurrent.atomic.{AtomicReference, AtomicBoolean}
import javafx.scene.layout.{Pane, HBox, BorderPane}
import javafx.scene.control.{Label, TitledPane, Accordion, Button}
import javafx.geometry.{Insets, Pos}
import javafx.event.{EventHandler, ActionEvent}
import javafx.scene.input.KeyCombination
import javafx.application.Platform
import java.util.concurrent.CountDownLatch
import javafx.beans.value.{ObservableValue, ChangeListener}
import scala.reflect.ClassTag
import java.io.File
import javafx.scene.image.Image
import com.ajjpj.adiagram.ui.{AScreenRect, AScreenPos}


/**
 * @author arno
 */
object JavaFxHelper {
  import scala.language.implicitConversions
  implicit def keyCombinationFromString(s: String) = KeyCombination.keyCombination(s)

  def enclosingNode[T <: Node](node: Node)(implicit ct: ClassTag[T]): Option[T] = {
    var candidate = node
    while(candidate.getParent != null) {
      candidate = candidate.getParent
      if(candidate.getClass == ct.runtimeClass) {
        return Some(candidate.asInstanceOf[T])
      }
    }
    None
  }

  def expandAccordionPaneFor(n: Node) {
    val titledPane = enclosingNode[TitledPane](n).get
    val accordion = enclosingNode[Accordion](titledPane).get
    accordion.setExpandedPane(titledPane)
  }

  def createUncollapsableAccordion() = {
    val result = new Accordion()
    result.expandedPaneProperty().addListener(new ChangeListener[TitledPane]() {
      override def changed(property: ObservableValue[_ <:  TitledPane], oldPane: TitledPane, newPane: TitledPane) {
        if (oldPane != null) {
          oldPane.setCollapsible(true)
        }
        if (newPane != null) {
          Platform.runLater(new Runnable() {
            override def run() {
              newPane.setCollapsible(false)
            }
          })
        }
      }
    })
    result
  }

  /**
   * runs a piece of code in the UI thread in the context of a Digest
   */
  def inUiThread(code: => Unit)(implicit digest: Digest) {
    if(Platform.isFxApplicationThread) {
      code
    }
    else {
      Platform.runLater(new Runnable() {
        def run() {
          digest.execute(code)
        }
      })
    }
  }

  /**
   * runs a piece of code in the UI thread <b>and waits for it</b> to finish, returning
   *  its result
   */
  def inUiThreadAndWait[T] (code: => T)(implicit digest: Digest): Option[T] = {
    val result = new AtomicReference[Option[T]]()
    val latch = new CountDownLatch(1)

    inUiThread {
      try {
        result.set(Some(code))
      }
      catch {
        case _: Throwable => result.set(None) //TODO logging
      }
      finally {
        latch.countDown()
      }
    }

    latch.await()
    result.get
  }

  /**
   * runs a piece of code in a background thread and passes the result of that to
   *  some other code that is run in the UI thread. This method takes care of
   *  error handling for both code blocks.
   */
  def inBackground[T](bgCode: => T, uiCode: T => Unit)(implicit digest: Digest) {
    new Thread() {
      override def run() {
        try {
          val result = bgCode
          inUiThread(uiCode(result))
        }
        catch {
          case exc: Throwable => exc.printStackTrace() //TODO exception handling
        }
      }
    }.start()
  }

  def image(path: String) = new Image(Thread.currentThread.getContextClassLoader.getResourceAsStream(path))

  //-------------------------------------

  class ButtonSpec(_text: => String, val clickId: String, val default: Boolean = false, val cancel: Boolean = false, enabled: => Boolean = true) {
    def text = _text
    def isEnabled = enabled
  }
  object ButtonSpec {
    def apply(text: => String, clickId: String, default: Boolean = false, cancel: Boolean = false, enabled: => Boolean = true) = new ButtonSpec(text, clickId, default=default, cancel=cancel, enabled=enabled)

    val idOk = "ok"
    val idCancel = "cancel"
    val idYes = "yes"
    val idNo = "no"

    def ok    (enabled: => Boolean = true) = ButtonSpec(text="OK",     clickId=idOk,     enabled=enabled, default=true)
    def cancel(enabled: => Boolean = true) = ButtonSpec(text="Cancel", clickId=idCancel, enabled=enabled, cancel=true)
    def yes   (enabled: => Boolean = true) = ButtonSpec(text="Yes",    clickId=idYes,    enabled=enabled)
    def no    (enabled: => Boolean = true) = ButtonSpec(text="No",     clickId=idNo,     enabled=enabled)

    val okCancel = List(ok(), cancel())
  }

  class ButtonPane(buttons: List[ButtonSpec], onClicked: String => Unit = (s: String) => {})(implicit digest: Digest) extends HBox with Unbindable {
    setAlignment(Pos.CENTER_RIGHT)
    setSpacing(8)

    buttons.foreach((b: ButtonSpec) => {
      val btn = new Button
      digest.bind(btn.textProperty, b.text)
      digest.bindBoolean(btn.disableProperty, ! b.isEnabled)
      getChildren.add(btn)
      if(b.default) btn.setDefaultButton(true)
      if(b.cancel) btn.setCancelButton(true)

      btn.setOnAction(new EventHandler[ActionEvent]{
        def handle(p1: ActionEvent) {
          onClicked(b.clickId)
        }
      })
    })

    override def unbind()(implicit digest: Digest) {
      import scala.collection.JavaConversions._

      getChildren.foreach(_ match {
        case btn: Button =>
          digest.unbind(btn.textProperty)
          digest.unbind(btn.disableProperty)
      })
    }
  }

  abstract class Dialog(owner: Window, title: => String)(implicit digest: Digest) extends Stage with Unbindable {
    initStyle(StageStyle.UTILITY)
    initModality(Modality.WINDOW_MODAL)
    initOwner(owner)

    digest.bind(titleProperty, title)

    private val theButtonPane = buttonPane
    private val theContent = content

    override def unbind()(implicit digest: Digest) {
      digest.unbind(titleProperty)
      theButtonPane.unbind()
      theContent match {
        case ub: Unbindable => ub.unbind()
        case _ =>
      }
    }

    private val pane = new BorderPane

    BorderPane.setMargin(theContent, new Insets(15, 15, 0, 15))
    BorderPane.setMargin(theButtonPane, new Insets(15))

    pane.setCenter(theContent)
    pane.setBottom(theButtonPane)
    setScene(new Scene(pane))

    override def hide {
      super.hide()
      unbind()
    }

    /**
     * override this to provide the 'body' of the dialog - default is empty
     */
    def content: Node = new Pane

    def buttonPane: ButtonPane
  }

  def showSingleClickDialog(owner: Window, title: String, body: Node, buttons: ButtonSpec*)(implicit digest: Digest): String = {
    var clickedId: String = buttons.find(_.cancel).map(_.clickId).get
    new Dialog(owner, title) {
      override def content = body
      override def buttonPane = new ButtonPane(buttons.toList, onClicked = s => {clickedId=s; close()})
    }.showAndWait()

    clickedId
  }

  def confirmOverwrite(file: File, owner: Window)(implicit digest: Digest): Boolean = {
    if(file.exists) {
      showOkCancelDialog(owner, "Overwrite?", "File " + file.getName + " exists. Overwrite it?")
    }
    else {
      true
    }
  }

  def showOkCancelDialog(owner: Window, title: String, content: String)(implicit digest: Digest): Boolean = showOkCancelDialog(owner, title, new Label(content))

  def showOkCancelDialog(owner: Window, title: String, contentNode: Node)(implicit digest: Digest): Boolean = {
    val result = new AtomicBoolean(false)

    val dialog = new Dialog(owner, title) {
      override def content = contentNode
      override def buttonPane = new ButtonPane(ButtonSpec.okCancel, onClicked = _ match {
        case ButtonSpec.idOk     => result.set(true); close()
        case ButtonSpec.idCancel => result.set(false); close()
      })
    }

    dialog.showAndWait()
    result.get()
  }
}



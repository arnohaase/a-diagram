package com.ajjpj.adiagram.ui.fw

import javafx.stage.{Modality, StageStyle, Stage}
import javafx.scene.{Scene, Node}
import java.util.concurrent.atomic.{AtomicReference, AtomicBoolean}
import javafx.scene.layout.{HBox, BorderPane}
import javafx.scene.control.Button
import javafx.geometry.Pos
import javafx.event.{EventHandler, ActionEvent}
import javafx.scene.input.KeyCombination
import com.ajjpj.adiagram.ui.fw.Digest
import javafx.application.Platform
import java.util.concurrent.CountDownLatch


/**
 * @author arno
 */
object JavaFxHelper {
  implicit def keyCombinationFromString(s: String) = KeyCombination.keyCombination(s)

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
        case _: Throwable => result.set(None)
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

  def showOkCancelDialog(owner: Stage, title: String, content: Node) {
    val result = new AtomicBoolean(false)

    val dialog = new Stage()
    dialog.initStyle(StageStyle.UTILITY)
    dialog.initModality(Modality.APPLICATION_MODAL)
    dialog.initOwner(owner)
    dialog.setTitle(title)

    val pane = new BorderPane()
    pane.setCenter(content)

    val buttonPane = new HBox()
    val okButton = new Button("OK")
    val cancelButton = new Button("Cancel")
    buttonPane.getChildren().addAll(okButton, cancelButton)
    buttonPane.setAlignment(Pos.CENTER_RIGHT)
    pane.setBottom(buttonPane)

    okButton.setOnAction(new EventHandler[ActionEvent] () {
      override def handle(p1: ActionEvent) {
        result.set(true)
        dialog.hide()
      }
    })
    cancelButton.setOnAction(new EventHandler[ActionEvent] () {
      override def handle(p1: ActionEvent) {
        result.set(false)
        dialog.hide()
      }
    })

    dialog.setScene(new Scene(pane))
    dialog.showAndWait()
    result.get()
  }
}



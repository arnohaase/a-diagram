package com.ajjpj.adiagram.ui.init

import com.ajjpj.adiagram.ui.fw.{Action, SimpleActionGroup, SimpleAction, Digest}
import com.ajjpj.adiagram.ui.fw.JavaFxHelper._
import scala.Some
import com.ajjpj.adiagram.ui.presentation.ADiagramController
import javafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}


/**
 * @author arno
 */
object ADiagramMenuBar {
  def create(ctrl: ADiagramController)(implicit digest: Digest) = Action.createMenuBar(editMenu, viewMenu(ctrl))

  private def editMenu(implicit digest: Digest) = {
    val undoAction = new SimpleAction(text="Undo " + digest.undoRedo.nextUndo.map(_.name).getOrElse(""), enabled=digest.undoRedo.hasUndo, accelerator = Some("Ctrl+Z"),       body={digest.undoRedo.undo()})
    val redoAction = new SimpleAction(text="Redo " + digest.undoRedo.nextRedo.map(_.name).getOrElse(""), enabled=digest.undoRedo.hasRedo, accelerator = Some("Ctrl+Shift+Z"), body={digest.undoRedo.redo()})

    new SimpleActionGroup(text="Edit", items=List(undoAction, redoAction))
  }

  private def viewMenu(ctrl: ADiagramController)(implicit digest: Digest) = {
    val zoomInAction  = new SimpleAction("Zoom In",  accelerator = Some(new KeyCodeCombination (KeyCode.PLUS,  KeyCombination.CONTROL_DOWN)), body={ctrl.zoom *= 1.5})
    val zoomOutAction = new SimpleAction("Zoom Out", accelerator = Some(new KeyCodeCombination (KeyCode.MINUS, KeyCombination.CONTROL_DOWN)), body={ctrl.zoom *= 1/1.5})

    new SimpleActionGroup(text="View", items=List(zoomInAction, zoomOutAction))
  }



//  val menuBar = {
//    val openAction = new SimpleAction(text="Open", accelerator = Some("Ctrl+O"), body= {
//      changeCounter.inc()
//      println(showOkCancelDialog(stage, "My Dialog", new Text(25, 25, "Hi There!")))
//    })
//    val saveAction = new SimpleAction(text="Save", body={})
//    val showAccordionAction = new SimpleAction("Show Accordion", accelerator = Some("Ctrl+L"), body={accordion.setVisible(! accordion.isVisible)})
//    val fullScreenAction = new SimpleAction("Full Screen", accelerator = Some("F11"), body={stage.setFullScreen(! stage.isFullScreen)})
//
//    val dummyAction = new SimpleAction(text="dummy", visible=c%5 != 4, enabled = c%2 != 0, body={})
//    val dummyAction2 = new SimpleAction(text="dummy2", body={c+=1})
//
//    val fileMenu = new SimpleActionGroup(text="File", items=List(openAction, saveAction))
//    val viewMenu = new SimpleActionGroup(text="View", items=List(showAccordionAction, fullScreenAction))
//    val dummyMenu = new SimpleActionGroup(text="Dummy", items=List(dummyAction, dummyAction2))
//
//    Action.createMenuBar(fileMenu, viewMenu, dummyMenu)
//  }

}

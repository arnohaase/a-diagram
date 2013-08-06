package com.ajjpj.adiagram.ui.init

import com.ajjpj.adiagram.ui.fw.{Action, SimpleActionGroup, SimpleAction, Digest}
import com.ajjpj.adiagram.ui.fw.JavaFxHelper._
import scala.Some
import com.ajjpj.adiagram.ui.presentation.ADiagramController
import javafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import com.ajjpj.adiagram.model.DiagramManipulation


/**
 * @author arno
 */
object ADiagramMenuBar {
  def create(ctrl: ADiagramController)(implicit digest: Digest) = Action.createMenuBar(diagramMenu(ctrl), editMenu, viewMenu(ctrl))

  private def diagramMenu(ctrl: ADiagramController)(implicit digest: Digest) = {
    val addBox  = new SimpleAction(text="Add Box",  accelerator = Some("Ctrl+B"), body={val box  = DiagramManipulation.addNewBox (ctrl.diagram, ctrl.selectedStyles.fillStyle, ctrl.selectedStyles.shadowStyle, ctrl.selectedStyles.textStyle); ctrl.selections.setSelection(box)})
    val addLine = new SimpleAction(text="Add Line", accelerator = Some("Ctrl+L"), body={val line = DiagramManipulation.addNewLine(ctrl.diagram, ctrl.selectedStyles.lineStyle, ctrl.selectedStyles.lineTextStyle);                              ctrl.selections.setSelection(line)})
    val addText = new SimpleAction(text="Add Text", accelerator = Some("Ctrl+T"), body={val text = DiagramManipulation.addNewText(ctrl.diagram, ctrl.selectedStyles.textStyle);                                                                 ctrl.selections.setSelection(text)})

    val delete = new SimpleAction(text="Delete", accelerator = Some(new KeyCodeCombination (KeyCode.DELETE)), enabled= ! (ctrl.selections.selectedShapes.isEmpty), body={DiagramManipulation.deleteSelection(ctrl)})

    new SimpleActionGroup(text="Diagram", items=List(addBox, addLine, addText, Action.SEPARATOR, delete))
  }

  private def editMenu(implicit digest: Digest) = {
    val undoAction = new SimpleAction(text="Undo " + digest.undoRedo.nextUndo.map(_.name).getOrElse(""), enabled=digest.undoRedo.hasUndo, accelerator = Some("Ctrl+Z"),       body={digest.undoRedo.undo()})
    val redoAction = new SimpleAction(text="Redo " + digest.undoRedo.nextRedo.map(_.name).getOrElse(""), enabled=digest.undoRedo.hasRedo, accelerator = Some("Ctrl+Shift+Z"), body={digest.undoRedo.redo()})

    new SimpleActionGroup(text="Edit", items=List(undoAction, redoAction))
  }

  private def viewMenu(ctrl: ADiagramController)(implicit digest: Digest) = {
    val zoomInAction  = new SimpleAction("Zoom In",  accelerator = Some(new KeyCodeCombination (KeyCode.PERIOD /*PLUS*/,  KeyCombination.CONTROL_DOWN)), body={ctrl.zoom *= 1.5})
    val zoomOutAction = new SimpleAction("Zoom Out", accelerator = Some(new KeyCodeCombination (KeyCode.MINUS, KeyCombination.CONTROL_DOWN)), body={ctrl.zoom *= 1/1.5})

    new SimpleActionGroup(text="View", items=List(zoomInAction, zoomOutAction))
  }
}


package com.ajjpj.adiagram.ui.init

import com.ajjpj.adiagram.ui.fw.{Action, SimpleActionGroup, SimpleAction, Digest}
import com.ajjpj.adiagram.ui.fw.JavaFxHelper._
import scala.Some
import com.ajjpj.adiagram.ui.presentation.ADiagramController
import javafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import com.ajjpj.adiagram.model.DiagramManipulation
import com.ajjpj.adiagram.model.diagram.{ABoxSpec, ATextSpec, ALineSpec}
import com.ajjpj.adiagram.model.style.{RoundPointedArrowLineEndSpec, RoundedCornerLineEndSpec}


/**
 * @author arno
 */
object ADiagramMenuBar {
  def create(ctrl: ADiagramController)(implicit digest: Digest) = Action.createMenuBar(diagramMenu(ctrl), editMenu, viewMenu(ctrl))

  private def diagramMenu(ctrl: ADiagramController)(implicit digest: Digest) = {
    val styles = ctrl.selectedStyles

    val addBox  = new SimpleAction(text="Add Box",  accelerator = Some("Ctrl+B"), body={val box  = DiagramManipulation.addNewBox (ctrl.diagram, styles.fillStyle, styles.shadowStyle, styles.textStyle);                         ctrl.selections.setSelection(box)})
    val addLine = new SimpleAction(text="Add Line", accelerator = Some("Ctrl+L"), body={val line = DiagramManipulation.addNewLine(ctrl.diagram, styles.lineStyle, styles.lineTextStyle, styles.startLineEnd, styles.endLineEnd); ctrl.selections.setSelection(line)})
    val addText = new SimpleAction(text="Add Text", accelerator = Some("Ctrl+T"), body={val text = DiagramManipulation.addNewText(ctrl.diagram, styles.textStyle);                                                               ctrl.selections.setSelection(text)})

    val delete = new SimpleAction(text="Delete", accelerator = Some(new KeyCodeCombination (KeyCode.DELETE)), enabled= ! ctrl.selections.selectedShapes.isEmpty, body={DiagramManipulation.deleteSelection(ctrl)})

    val createDummy = new SimpleAction(text="Create Dummy", accelerator = Some("Ctrl+Alt+D"), body = createDummyDiagram(ctrl))

    new SimpleActionGroup(text="Diagram", items=List(addBox, addLine, addText, Action.SEPARATOR, delete, Action.SEPARATOR, createDummy))
  }

  private def createDummyDiagram(ctrl: ADiagramController)(implicit digest: Digest) {
    val fillStyle     = ctrl.styleRepository.fillStyles.iterator.next()   //    new FillStyle(new LinearGradient(0.3, 0, .7, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.LIGHTBLUE), new Stop(1, Color.AZURE)))
    val shadowStyle   = ctrl.styleRepository.shadowStyles.iterator.next() // = new ShadowStyle(6, 6, 16, BlurType.GAUSSIAN, Color.color(.5, .5, .5))
    val textStyle     = ctrl.styleRepository.textStyles.find(_.name == "Box").get
    val lineTextStyle = ctrl.styleRepository.textStyles.find(_.name == "Line").get
    val lineStyle     = ctrl.styleRepository.lineStyles.find(_.width > 3.5).get

    val box1 = new ABoxSpec((100.0, 200.0), (250.0, 80.0), Some("Hi Ho!"), fillStyle, shadowStyle, textStyle)
    val box2 = new ABoxSpec((400.0, 400.0), (250.0, 80.0), Some("Yeah!"),  fillStyle, shadowStyle, textStyle)
    ctrl.diagram += box1
    ctrl.diagram += box2
    ctrl.diagram += new ALineSpec((1400.0, 100.0), (900.0, 500.0), Some("Arrow Text"), lineStyle, lineTextStyle, RoundedCornerLineEndSpec, RoundPointedArrowLineEndSpec)
    ctrl.diagram += new ATextSpec((100.0, 600.0), (300.0, 80.0), "Hey Dude", textStyle)

    val connectingLine = new ALineSpec((0.0, 0.0), (0.0, 0.0), Some("Connecting"), lineStyle, lineTextStyle, RoundedCornerLineEndSpec, RoundPointedArrowLineEndSpec)
    connectingLine.bindStartPoint(box1)
    connectingLine.bindEndPoint  (box2)

    ctrl.diagram += connectingLine
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


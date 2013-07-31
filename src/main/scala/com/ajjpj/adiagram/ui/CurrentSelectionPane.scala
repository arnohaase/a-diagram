package com.ajjpj.adiagram.ui

import javafx.scene.layout.Pane
import com.ajjpj.adiagram.model.{ALineSpec, ATextSpec, ABoxSpec, AShapeSpec}
import com.ajjpj.adiagram.ui.fw.JavaFxHelper

/**
 * @author arno
 */
class CurrentSelectionPane(selections: SelectionTracker) extends Pane {
  selections.selectionChangeListeners += onSelectionChanged

  initForCurrentSelection()

  private def initForCurrentSelection() {
    getChildren.clear()

    selections.selectedShapes.toList match {
      case List(box: ABoxSpec) => println("box")
      case List(box: ALineSpec) => println("line")
      case List(box: ATextSpec) => println("text")
      case _ => println ("TODO")
    }
  }


  private def onSelectionChanged(oldSel: Traversable[AShapeSpec], newSel: Traversable[AShapeSpec]) {
    initForCurrentSelection()

    if(! newSel.isEmpty) {
      JavaFxHelper.expandAccordionPaneFor(this)
    }
  }
}

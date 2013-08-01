package com.ajjpj.adiagram.ui

import javafx.scene.layout.Pane
import com.ajjpj.adiagram.model.{ALineSpec, ATextSpec, ABoxSpec, AShapeSpec}
import com.ajjpj.adiagram.ui.fw.{Digest, JavaFxHelper}
import com.ajjpj.adiagram.ui.forms.BoxDetailsForm

/**
 * @author arno
 */
class CurrentSelectionPane(selections: SelectionTracker)(implicit digest: Digest) extends Pane {
  selections.selectionChangeListeners += onSelectionChanged

  initForCurrentSelection()

  private val boxDetails = new BoxDetailsForm()

  private def initForCurrentSelection() {
    getChildren.clear() //TODO unbind old!

    selections.selectedShapes.toList match {
      case List(box: ABoxSpec) =>
        getChildren.add(boxDetails)
      boxDetails.bind(box)
        println("box")
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

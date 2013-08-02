package com.ajjpj.adiagram.ui

import javafx.scene.layout.Pane
import com.ajjpj.adiagram.model.{ALineSpec, ATextSpec, ABoxSpec, AShapeSpec}
import com.ajjpj.adiagram.ui.fw.{Digest, JavaFxHelper}
import com.ajjpj.adiagram.ui.forms.{LineDetailsForm, TextDetailsForm, AbstractForm, BoxDetailsForm}


/**
 * @author arno
 */
class CurrentSelectionPane(selections: SelectionTracker)(implicit digest: Digest) extends Pane {
  private var curForm: Option[AbstractForm] = None

  selections.selectionChangeListeners += onSelectionChanged

  initForCurrentSelection()


  private def initForCurrentSelection() {
    curForm match {
      case Some(u) => u.unbind()
      case None =>
    }
    getChildren.clear()

    curForm = selections.selectedShapes.toList match {
      case List(box:  ABoxSpec)  => Some(new BoxDetailsForm(box))
      case List(text: ATextSpec) => Some(new TextDetailsForm(text))
      case List(line: ALineSpec) => Some(new LineDetailsForm(line))
      case _ => None
    }

    curForm.foreach(getChildren.add)
  }


  private def onSelectionChanged(oldSel: Traversable[AShapeSpec], newSel: Traversable[AShapeSpec]) {
    initForCurrentSelection()

    if(! newSel.isEmpty) {
      JavaFxHelper.expandAccordionPaneFor(this)
    }
  }
}
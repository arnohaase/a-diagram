package com.ajjpj.adiagram.ui

import javafx.scene.layout.Pane
import javafx.scene.control.Label
import com.ajjpj.adiagram.model.AShapeSpec
import com.ajjpj.adiagram.ui.fw.JavaFxHelper

/**
 * @author arno
 */
class CurrentSelectionPane(selections: SelectionTracker) extends Pane {
  val label = new Label ("... selection ...") //TODO '<no selection>'
  getChildren().addAll(label)

  selections.selectionChangeListeners += onSelectionChanged


  private def onSelectionChanged(oldSel: Traversable[AShapeSpec], newSel: Traversable[AShapeSpec]) {
    label.setText(newSel.toString)

    if(! newSel.isEmpty) {
      JavaFxHelper.expandAccordionPaneFor(this)
    }
  }
}

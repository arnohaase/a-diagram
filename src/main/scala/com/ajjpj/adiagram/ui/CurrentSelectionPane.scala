package com.ajjpj.adiagram.ui

import javafx.scene.layout.Pane
import javafx.scene.control.Label

/**
 * @author arno
 */
class CurrentSelectionPane(selections: SelectionTracker) extends Pane {
  getChildren().addAll(new Label("... selection ..."))

}

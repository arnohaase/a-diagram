package com.ajjpj.adiagram.ui.presentation

import javafx.collections.FXCollections
import javafx.scene.layout.Pane
import com.ajjpj.adiagram.ui.fw.Digest

/**
 * @author arno
 */
class DiagramRootContainer(implicit digest: Digest) extends Pane {
  digest.registerPostprocessor(sortByZ _)

  private var curOverlay: Option[Pane] = None

  def showOverlay(overlay: Pane) = {
    clearOverlay()

    val decorated = new Pane() with ZOrdered {
      getChildren.add(overlay)
      def z = Integer.MAX_VALUE
    }

    getChildren.add(decorated)
    curOverlay = Some(decorated)
  }

  def clearOverlay() = curOverlay match {
    case Some(o) => getChildren.remove(o); curOverlay = None
    case None =>
  }

  private def sortByZ() {
    FXCollections.sort(getChildren, ByZComparator)
  }
}


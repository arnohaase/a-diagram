package com.ajjpj.adiagram.ui.presentation

import javafx.collections.FXCollections
import javafx.scene.layout.Pane
import com.ajjpj.adiagram.ui.fw.Digest
import com.ajjpj.adiagram.ui.AScreenRect
import javafx.scene.Node

/**
 * @author arno
 */
class DiagramRootContainer(implicit digest: Digest) extends Pane {
  digest.registerPostprocessor(sortByZ)

  private var curOverlay: Option[DiagramOverlay] = None

  def setOverlay(overlay: DiagramOverlay) = {
    clearOverlay()

    val decorated = new DiagramOverlay() with ZOrdered {
      getChildren.add(overlay)
      override def z = Integer.MAX_VALUE
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

  def boundsInRoot(n: Node): AScreenRect = {
    if(n == this) {
      AScreenRect((0.0, 0.0), (1.0, 1.0))
    }
    else {
      val local = AScreenRect((n.getLayoutX, n.getLayoutY), n.getLayoutBounds.getWidth, n.getLayoutBounds.getHeight)
      val parentBounds = boundsInRoot(n.getParent)
      AScreenRect(local.topLeft + parentBounds.topLeft, local.bottomRight + parentBounds.topLeft)
    }
  }
}

trait DiagramOverlay extends Pane {
}


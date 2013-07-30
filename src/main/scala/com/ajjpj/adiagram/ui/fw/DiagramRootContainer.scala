package com.ajjpj.adiagram.ui.fw

import javafx.scene.Node
import javafx.collections.FXCollections
import java.util.Comparator
import javafx.scene.layout.Pane

/**
 * @author arno
 */
class DiagramRootContainer(implicit digest: Digest) extends Pane {
  digest.registerPostprocessor(sortByZ _)

  val byZComparator = new Comparator[Node] {
    def compare(o1: Node, o2: Node): Int = (o1, o2) match {
      case (n1: ZOrdered, n2: ZOrdered) =>
        (n1.z - n2.z) match {
          case 0 => System.identityHashCode(o1) - System.identityHashCode(o2)
          case n => n
        }
      case (n1: ZOrdered, _) => 1
      case (_, n2: ZOrdered) => -1
      case _ =>
        System.identityHashCode(o1) - System.identityHashCode(o2)
    }
  }

  private def sortByZ() {
    FXCollections.sort(getChildren, byZComparator)
  }
}

trait ZOrdered {
  def z: Int
}
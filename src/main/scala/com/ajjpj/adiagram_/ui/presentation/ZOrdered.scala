package com.ajjpj.adiagram_.ui.presentation

import javafx.scene.canvas.Canvas
import java.util.Comparator
import javafx.scene.Node

/**
 * @author arno
 */
trait ZOrdered {
   def z: Int
 }

class CanvasWithDerivedZOrder (inner: ZOrdered) extends Canvas with ZOrdered {
  override def z = inner.z
}

object ByZComparator extends Comparator[Node] {
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

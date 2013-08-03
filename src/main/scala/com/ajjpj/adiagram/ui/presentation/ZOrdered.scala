package com.ajjpj.adiagram.ui.presentation

import javafx.scene.canvas.Canvas

/**
 * @author arno
 */
trait ZOrdered {
   def z: Int
 }

class CanvasWithDerivedZOrder (inner: ZOrdered) extends Canvas with ZOrdered {
  override def z = inner.z
}
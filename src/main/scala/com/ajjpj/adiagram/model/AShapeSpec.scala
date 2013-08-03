package com.ajjpj.adiagram.model

import com.ajjpj.adiagram.render.shapes.AShape
import com.ajjpj.adiagram.geometry.{ADim, APoint}
import javafx.scene.canvas.Canvas
import com.ajjpj.adiagram.ui.fw.{JavaFxHelper, Digest}
import com.ajjpj.adiagram.render.base.{PartialImage, PartialImageWithShadow}
import com.ajjpj.adiagram.ui.presentation.{ZOrdered, DiagramRootContainer}


/**
 * @author arno
 */
trait AShapeSpec extends ZOrdered {
  private var _changeCounter = 0
  def changeCounter = _changeCounter
  var z = 0 //TODO changes only in an 'atomicUpdate' call

  //TODO Node.pickOnBounds (ignore transparent pixels for purposes of mouse event routing) --> does not appear to work?!

  def atomicUpdate(code: => Unit) = { //TODO add implicit parameter of a protected type to all update methods in subclasses --> prevent them from being called outside 'atomicUpdate'
    code
    _changeCounter += 1
  }

  def snapshot: ShapeSpecReRenderSnapshot
  def contains(p: APoint): Boolean = shape.bounds contains p //TODO shapeCanvas.contains(p.x - shapeCanvas.getLayoutX, p.y - shapeCanvas.getLayoutY) //TODO special JavaFX API to ignore transparent parts?

  def pos: APoint
  def shape: AShape

  /**
   * dimension must be compatible with 'resizeTo'
   */
  def boundsForResizing = shape.bounds
  def resizeBy(delta: ADim): Unit
  final def moveBy(delta: APoint) {
    doMoveBy(delta)
  }

  protected def doMoveBy(delta: APoint): Unit
}

trait ShapeSpecReRenderSnapshot

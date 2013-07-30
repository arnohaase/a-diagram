package com.ajjpj.adiagram.model

import com.ajjpj.adiagram.render.shapes.AShape
import com.ajjpj.adiagram.geometry.{ADim, APoint}
import javafx.scene.canvas.Canvas
import com.ajjpj.adiagram.ui.fw.{DiagramRootContainer, JavaFxHelper, ZOrdered, Digest}
import com.ajjpj.adiagram.render.base.{PartialImage, PartialImageWithShadow}


/**
 * @author arno
 */
trait AShapeSpec {
  var z = 0 //TODO changes only in an 'atomicUpdate' call

  private val shapeCanvas = new Canvas() with ZOrdered {
    override def z = AShapeSpec.this.z
  }
  private val shadowCanvas = new Canvas()

  //TODO Node.pickOnBounds (ignore transparent pixels for purposes of mouse event routing) --> does not appear to work?!

  private var shapeOffset = APoint.ZERO //TODO special Canvas subclass? Mix-In Trait? That can also deal with Z ordering
  private var shadowOffset = APoint.ZERO

  def register(parent: DiagramRootContainer)(implicit digest: Digest) = {
    parent.getChildren.addAll(shadowCanvas, shapeCanvas)
    atomicUpdate {} // initial rendering
  }
  def unregister(parent: DiagramRootContainer) = parent.getChildren.removeAll(shadowCanvas, shapeCanvas)

  def atomicUpdate(code: => Unit)(implicit digest: Digest) = { //TODO add implicit parameter of a protected type to all update methods in subclasses --> prevent them from being called outside 'atomicUpdate'
    try {
      code
    }
    finally {
      def render: PartialImageWithShadow = shape.render

      def drawOnCanvas(i: PartialImage, c: Canvas) {
        c.setWidth (i.img.getWidth)
        c.setHeight (i.img.getHeight)
        c.getGraphicsContext2D.clearRect(0, 0, c.getWidth, c.getHeight) //TODO is there a better way for this?
        c.getGraphicsContext2D.drawImage(i.img, 0, 0)
      }

      JavaFxHelper.inBackground(render, (pi: PartialImageWithShadow) => {
        shapeOffset = pi.shape.renderOffset
        drawOnCanvas(pi.shape, shapeCanvas)
        pi.shadow match {
          case Some(sh) =>
            shadowOffset = sh.renderOffset
            drawOnCanvas(sh, shadowCanvas)
          case None =>
            shadowCanvas.setWidth(0)
            shadowCanvas.setHeight(0)
        }
        moveBy(APoint.ZERO) // deal with potentially changed render offsets
      })
    }
  }

  def contains(p: APoint) = shapeCanvas.contains(p.x - shapeCanvas.getLayoutX, p.y - shapeCanvas.getLayoutY) //TODO special JavaFX API to ignore transparent parts?

  protected def pos: APoint
  protected def shape: AShape

  /**
   * dimension must be compatible with 'resizeTo'
   */
  def boundsForResizing = shape.bounds
  def resizeBy(delta: ADim): Unit
  final def moveBy(delta: APoint) {
    doMoveBy(delta)
    shapeCanvas.setLayoutX((pos + shapeOffset).x)
    shapeCanvas.setLayoutY((pos + shapeOffset).y)
    shadowCanvas.setLayoutX(((pos + shadowOffset).x))
    shadowCanvas.setLayoutY(((pos + shadowOffset).y))
  }

  protected def doMoveBy(delta: APoint): Unit
}

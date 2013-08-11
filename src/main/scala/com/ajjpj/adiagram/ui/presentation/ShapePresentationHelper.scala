package com.ajjpj.adiagram.ui.presentation

import com.ajjpj.adiagram.render.base.{PartialImageWithShadow, PartialImage}
import javafx.scene.canvas.Canvas
import com.ajjpj.adiagram.ui.{Zoom, AScreenPos}
import com.ajjpj.adiagram.geometry.APoint


/**
 * @author arno
 */
object ShapePresentationHelper {
  private def drawOnCanvas(i: PartialImage, c: Canvas) {
    c.setWidth (i.img.getWidth)
    c.setHeight (i.img.getHeight)
    c.getGraphicsContext2D.clearRect(0, 0, c.getWidth, c.getHeight) //TODO is there a better way for this?
    c.getGraphicsContext2D.drawImage(i.img, 0, 0)
  }

  def refreshPos(canvas: Canvas, pos: APoint, offset: APoint, zoom: Zoom): Unit = {
    val canvasPos  = AScreenPos.fromModel(pos + offset, zoom)

    canvas.setLayoutX (canvasPos.x)
    canvas.setLayoutY (canvasPos.y)
  }

  def refreshPos(shapeCanvas: Canvas, shadowCanvas: Canvas, pos: APoint, shapeOffset: APoint, shadowOffset: APoint, zoom: Zoom): Unit = {
    refreshPos(shapeCanvas,  pos, shapeOffset,  zoom)
    refreshPos(shadowCanvas, pos, shadowOffset, zoom)
  }

  def drawShapeOnCanvas(pi: PartialImageWithShadow, pos: APoint, shapeCanvas: Canvas, shadowCanvas: Canvas, zoom: Zoom) {
    drawOnCanvas(pi.shape, shapeCanvas)
    refreshPos(shapeCanvas, pos, pi.shape.renderOffset, zoom)
    pi.shadow match {
      case Some(sh) =>
        drawOnCanvas(sh, shadowCanvas)
        refreshPos(shadowCanvas, pos, sh.renderOffset, zoom)
      case None =>
        shadowCanvas.setWidth(0)
        shadowCanvas.setHeight(0)
    }
  }
}

package com.ajjpj.adiagram_.ui.mouse

import com.ajjpj.adiagram_.ui.presentation.{ZOrdered}
import com.ajjpj.adiagram_.ui.fw.{SystemConfiguration, Unbindable, Digest}
import com.ajjpj.adiagram_.ui.{ADiagramController, AScreenPos, ResizeDirection}
import com.ajjpj.adiagram_.geometry.ARect
import javafx.scene.shape.Rectangle
import javafx.scene.paint.Color


/**
 * @author arno
 */
private[mouse] class BoxHandles(ctrl: ADiagramController)(implicit digest: Digest) extends Unbindable {
  private val topLeftHandle     = new BoxSelectionHandle(ResizeDirection(left = true,  top = true,  right = false, bottom = false))
  private val topRightHandle    = new BoxSelectionHandle(ResizeDirection(left = false, top = true,  right = true,  bottom = false))
  private val bottomLeftHandle  = new BoxSelectionHandle(ResizeDirection(left = true,  top = false, right = false, bottom = true))
  private val bottomRightHandle = new BoxSelectionHandle(ResizeDirection(left = false, top = false, right = true,  bottom = true))

  private val handles = List(topLeftHandle, topRightHandle, bottomLeftHandle, bottomRightHandle)

  private def boundingRect = ARect.containingRect(ctrl.selections.selectedShapes.map(_.boundsForResizing))

  digest.bindDouble(topLeftHandle.    xProperty, AScreenPos.fromModel(boundingRect.topLeft, ctrl.zoom).x     - SystemConfiguration.dragHandleSize/2 - SystemConfiguration.distanceOfHandlesFromShapesXY)
  digest.bindDouble(topLeftHandle.    yProperty, AScreenPos.fromModel(boundingRect.topLeft, ctrl.zoom).y     - SystemConfiguration.dragHandleSize/2 - SystemConfiguration.distanceOfHandlesFromShapesXY)
  digest.bindDouble(topRightHandle.   xProperty, AScreenPos.fromModel(boundingRect.topRight, ctrl.zoom).x    - SystemConfiguration.dragHandleSize/2 + SystemConfiguration.distanceOfHandlesFromShapesXY)
  digest.bindDouble(topRightHandle.   yProperty, AScreenPos.fromModel(boundingRect.topRight, ctrl.zoom).y    - SystemConfiguration.dragHandleSize/2 - SystemConfiguration.distanceOfHandlesFromShapesXY)
  digest.bindDouble(bottomLeftHandle. xProperty, AScreenPos.fromModel(boundingRect.bottomLeft, ctrl.zoom).x  - SystemConfiguration.dragHandleSize/2 - SystemConfiguration.distanceOfHandlesFromShapesXY)
  digest.bindDouble(bottomLeftHandle. yProperty, AScreenPos.fromModel(boundingRect.bottomLeft, ctrl.zoom).y  - SystemConfiguration.dragHandleSize/2 + SystemConfiguration.distanceOfHandlesFromShapesXY)
  digest.bindDouble(bottomRightHandle.xProperty, AScreenPos.fromModel(boundingRect.bottomRight, ctrl.zoom).x - SystemConfiguration.dragHandleSize/2 + SystemConfiguration.distanceOfHandlesFromShapesXY)
  digest.bindDouble(bottomRightHandle.yProperty, AScreenPos.fromModel(boundingRect.bottomRight, ctrl.zoom).y - SystemConfiguration.dragHandleSize/2 + SystemConfiguration.distanceOfHandlesFromShapesXY)

  handles.foreach(h => {
    digest.bindBoolean(h.visibleProperty, ctrl.selections.selectedShapes.size == 1 && ! ctrl.selections.selectionIsSingleLine)
    ctrl.root.getChildren.add(h)
  })

  def resizeDirFor(p: AScreenPos): Option[ResizeDirection] = handles.find(h => h.isVisible && h.contains(p.x, p.y)).map(_.dir)

  override def unbind()(implicit digest: Digest) {
    handles.foreach(h => {
      digest.unbind(h.xProperty)
      digest.unbind(h.yProperty)
      digest.unbind(h.visibleProperty)
      ctrl.root.getChildren.remove(h)
    })
  }

  private class BoxSelectionHandle(val dir: ResizeDirection) extends Rectangle with ZOrdered {
    setFill(Color.BLACK)
    //    setStroke(Color.WHITE)
    setWidth(SystemConfiguration.dragHandleSize)
    setHeight(SystemConfiguration.dragHandleSize)

    override def z = Integer.MAX_VALUE
  }
}

package com.ajjpj.adiagram.ui

import com.ajjpj.adiagram.model.{ALineSpec, AShapeSpec, ADiagram}
import javafx.scene.shape.Rectangle
import javafx.scene.paint.Color
import com.ajjpj.adiagram.geometry.{Angle, APoint, ARect}
import com.ajjpj.adiagram.ui.fw.{SystemConfiguration, Digest}
import scala.reflect.ClassTag
import com.ajjpj.adiagram.ui.presentation.{ZOrdered, DiagramRootContainer}

/**
 * @author arno
 */
class SelectionTracker (diagram: ADiagram, root: DiagramRootContainer)(implicit digest: Digest) {
  val HANDLE_SIZE = 10

  type ChangeListener = (Traversable[AShapeSpec], Traversable[AShapeSpec]) => Unit

  private var _selectedShapes = Set[AShapeSpec]()
  var selectionChangeListeners = Set[ChangeListener] ()

  private val topLeftHandle     = new BoxSelectionHandle(ResizeDirection(left = true,  top = true,  right = false, bottom = false))
  private val topRightHandle    = new BoxSelectionHandle(ResizeDirection(left = false, top = true,  right = true,  bottom = false))
  private val bottomLeftHandle  = new BoxSelectionHandle(ResizeDirection(left = true,  top = false, right = false, bottom = true))
  private val bottomRightHandle = new BoxSelectionHandle(ResizeDirection(left = false, top = false, right = true,  bottom = true))

  private val lineStartHandle = new LineSelectionHandle()
  private val lineEndHandle = new LineSelectionHandle()

  private val boxHandles = List(topLeftHandle, topRightHandle, bottomLeftHandle, bottomRightHandle)
  root.getChildren.addAll(
    lineStartHandle, lineEndHandle,
    topLeftHandle, topRightHandle, bottomLeftHandle, bottomRightHandle
  )

  def selectedShapes = _selectedShapes

  private def withChangeEvent[T] (code: => T): T = {
    val oldSel = selectedShapes
    val result = code
    if(oldSel != selectedShapes) {
      selectionChangeListeners.foreach(l => l(oldSel, selectedShapes))
    }
    result
  }

  def setSelection(sel: AShapeSpec):           Unit = withChangeEvent { _selectedShapes = Set(sel) }
  def setSelection(sel: Iterable[AShapeSpec]): Unit = withChangeEvent { _selectedShapes = Set() ++ sel }

  def addSelection(sel: AShapeSpec):           Unit = withChangeEvent { _selectedShapes += sel }
  def addSelection(sel: Iterable[AShapeSpec]): Unit = withChangeEvent { _selectedShapes ++= sel }

  def removeSelection(sel: AShapeSpec):           Unit = withChangeEvent { _selectedShapes -= sel }
  def removeSelection(sel: Iterable[AShapeSpec]): Unit = withChangeEvent { _selectedShapes --= sel }

  def clearSelection() = withChangeEvent { _selectedShapes = Set() }

  def resizeDirFor (p: APoint): Option[ResizeDirection] =
    if(topLeftHandle.isVisible)
      boxHandles.find(h => h.contains(p.x, p.y)).map(_.dir)
    else
      None

  def lineEndFor (p: APoint): Option[Boolean] = p match {
    case _ if !lineStartHandle.isVisible => None
    case _ if lineStartHandle contains (p.x, p.y) => Some(true)
    case _ if lineEndHandle   contains (p.x, p.y) => Some(false)
    case _ => None
  }

  digest.bindBoolean(lineStartHandle.visibleProperty(), selectionIsSingleLine)
  digest.bindBoolean(lineEndHandle  .visibleProperty(), selectionIsSingleLine)

  digest.bindDouble(lineStartHandle.xProperty(), lineStartPointForSelectionHandle.x - HANDLE_SIZE/2)
  digest.bindDouble(lineStartHandle.yProperty(), lineStartPointForSelectionHandle.y - HANDLE_SIZE/2)
  digest.bindDouble(lineEndHandle.  xProperty(), lineEndPointForSelectionHandle.x   - HANDLE_SIZE/2)
  digest.bindDouble(lineEndHandle.  yProperty(), lineEndPointForSelectionHandle.y   - HANDLE_SIZE/2)

  private def lineStartPointForSelectionHandle = if (selectionIsSingleLine) endPointWithDistance(singleSelectedLine.p0, singleSelectedLine.p1) else APoint(0, 0)
  private def lineEndPointForSelectionHandle   = if (selectionIsSingleLine) endPointWithDistance(singleSelectedLine.p1, singleSelectedLine.p0) else APoint(0, 0)

  private def endPointWithDistance(p0: APoint, p1: APoint) = p0 + (Angle.fromLine(p0, p1), -SystemConfiguration.distanceOfHandlesFromShapes)

  private def selectionIsSingleLine =
    if(selectedShapes.size != 1)
      false
    else {
      val sh = selectedShapes.iterator.next()
      sh.isInstanceOf[ALineSpec]
    }

  def singleSelectedLine = singleSelection[ALineSpec].get

  def singleSelection[T](implicit ct: ClassTag[T]): Option[T] = selectedShapes.toList match {
    case h :: Nil if ct.runtimeClass.isInstance(h) => Some(h.asInstanceOf[T])
    case _ => None
  }


  private def selectionRect = if(selectedShapes.isEmpty)
      ARect.fromCoordinates(0, 0, 0, 0)
    else
      ARect.containingRect(selectedShapes.map(_.boundsForResizing))

  digest.bindBoolean(topLeftHandle.    visibleProperty(), ! selectedShapes.isEmpty && !selectionIsSingleLine)
  digest.bindBoolean(topRightHandle.   visibleProperty(), ! selectedShapes.isEmpty && !selectionIsSingleLine)
  digest.bindBoolean(bottomLeftHandle. visibleProperty(), ! selectedShapes.isEmpty && !selectionIsSingleLine)
  digest.bindBoolean(bottomRightHandle.visibleProperty(), ! selectedShapes.isEmpty && !selectionIsSingleLine)

  digest.bindDouble(topLeftHandle.    xProperty, selectionRect.topLeft.x     - HANDLE_SIZE/2 - SystemConfiguration.distanceOfHandlesFromShapesXY)
  digest.bindDouble(topLeftHandle.    yProperty, selectionRect.topLeft.y     - HANDLE_SIZE/2 - SystemConfiguration.distanceOfHandlesFromShapesXY)
  digest.bindDouble(topRightHandle.   xProperty, selectionRect.topRight.x    - HANDLE_SIZE/2 + SystemConfiguration.distanceOfHandlesFromShapesXY)
  digest.bindDouble(topRightHandle.   yProperty, selectionRect.topRight.y    - HANDLE_SIZE/2 - SystemConfiguration.distanceOfHandlesFromShapesXY)
  digest.bindDouble(bottomLeftHandle. xProperty, selectionRect.bottomLeft.x  - HANDLE_SIZE/2 - SystemConfiguration.distanceOfHandlesFromShapesXY)
  digest.bindDouble(bottomLeftHandle. yProperty, selectionRect.bottomLeft.y  - HANDLE_SIZE/2 + SystemConfiguration.distanceOfHandlesFromShapesXY)
  digest.bindDouble(bottomRightHandle.xProperty, selectionRect.bottomRight.x - HANDLE_SIZE/2 + SystemConfiguration.distanceOfHandlesFromShapesXY)
  digest.bindDouble(bottomRightHandle.yProperty, selectionRect.bottomRight.y - HANDLE_SIZE/2 + SystemConfiguration.distanceOfHandlesFromShapesXY)

  class LineSelectionHandle extends Rectangle with ZOrdered {
    setFill(Color.BLACK)
    setWidth(HANDLE_SIZE)
    setHeight(HANDLE_SIZE)

    override def z = Integer.MAX_VALUE
  }

  class BoxSelectionHandle(val dir: ResizeDirection) extends Rectangle with ZOrdered {
    setFill(Color.BLACK)
//    setStroke(Color.WHITE)
    setWidth(HANDLE_SIZE)
    setHeight(HANDLE_SIZE)

    override def z = Integer.MAX_VALUE
  }
}

case class ResizeDirection(left: Boolean, top: Boolean, right: Boolean, bottom: Boolean)
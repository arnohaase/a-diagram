package com.ajjpj.adiagram_.ui

import com.ajjpj.adiagram_.ui.fw.Digest
import scala.reflect.ClassTag
import com.ajjpj.adiagram_.ui.presentation.DiagramRootContainer
import com.ajjpj.adiagram_.model.diagram.{ATextSpec, AShapeSpec, ALineSpec, ADiagram}

/**
 * @author arno
 */
class SelectionTracker (diagram: ADiagram, root: DiagramRootContainer, ctrl: ADiagramController)(implicit digest: Digest) {
  val HANDLE_SIZE = 10

  type ChangeListener = (Traversable[AShapeSpec], Traversable[AShapeSpec]) => Unit

  private var _selectedShapes = Set[AShapeSpec]()
  var selectionChangeListeners = Set[ChangeListener] ()


  def selectedShapes = _selectedShapes

  private def withChangeEvent[T] (code: => T): T = {
    val oldSel = selectedShapes
    val result = code
    if(oldSel != selectedShapes) {
      selectionChangeListeners.foreach(l => l(oldSel, selectedShapes))
    }

    (oldSel -- selectedShapes).foreach(_ match {
      case t: ATextSpec if t.text.trim().isEmpty => diagram -= t // 'garbage collect' empty text elements
      case _ =>
    })

    result
  }

  def setSelection(sel: AShapeSpec):           Unit = withChangeEvent { _selectedShapes = Set(sel) }
  def setSelection(sel: Iterable[AShapeSpec]): Unit = withChangeEvent { _selectedShapes = Set() ++ sel }

  def addSelection(sel: AShapeSpec):           Unit = withChangeEvent { _selectedShapes += sel }
  def addSelection(sel: Iterable[AShapeSpec]): Unit = withChangeEvent { _selectedShapes ++= sel }

  def removeSelection(sel: AShapeSpec):           Unit = withChangeEvent { _selectedShapes -= sel }
  def removeSelection(sel: Iterable[AShapeSpec]): Unit = withChangeEvent { _selectedShapes --= sel }

  def clearSelection() = withChangeEvent { _selectedShapes = Set() }

  def resizeDirFor (p: AScreenPos): Option[ResizeDirection] = None //TODO remove this

  def lineEndFor (p: AScreenPos): Option[Boolean] = None //TODO remove this

  def selectionIsSingleLine =
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
}

case class ResizeDirection(left: Boolean, top: Boolean, right: Boolean, bottom: Boolean) //TODO move to ResizableState
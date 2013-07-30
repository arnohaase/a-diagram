package com.ajjpj.adiagram.model

import com.ajjpj.adiagram.ui.fw.{DiagramRootContainer, Digest}

/**
 * @author arno
 */
class ADiagram {
  private var _elements = Set[AShapeSpec]()

  private var root: Option[DiagramRootContainer] = None

  def initRootContainer (newRoot: DiagramRootContainer)(implicit digest: Digest) = {
    _elements.foreach(unregister)
    this.root = Some(newRoot)
    _elements.foreach(register)
  }

  private def register  (el: AShapeSpec)(implicit digest: Digest) = root match {case Some(r) => el.register(r);   case None =>}
  private def unregister(el: AShapeSpec)(implicit digest: Digest) = root match {case Some(r) => el.unregister(r); case None =>}

  def elements = _elements

  def +=(el: AShapeSpec)(implicit digest: Digest) = {_elements += el; register(el) }
  def -=(el: AShapeSpec)(implicit digest: Digest) = {_elements -= el; unregister(el) }
}

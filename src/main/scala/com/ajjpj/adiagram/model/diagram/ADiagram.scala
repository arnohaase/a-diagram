package com.ajjpj.adiagram.model.diagram

/**
 * @author arno
 */
class ADiagram {
  private var _elements = Set[AShapeSpec]()

  def elements = _elements

  def ++=(els: Traversable[AShapeSpec]) = _elements ++= els
  def +=(el: AShapeSpec) = _elements += el
  def -=(el: AShapeSpec) = _elements -= el
}

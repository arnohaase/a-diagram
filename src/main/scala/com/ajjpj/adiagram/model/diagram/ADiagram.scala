package com.ajjpj.adiagram.model.diagram

/**
 * @author arno
 */
class ADiagram {
  private var _elements = Set[AShapeSpec]()

  def elements = _elements

  def +=(el: AShapeSpec) = _elements += el
  def -=(el: AShapeSpec) = _elements -= el
}

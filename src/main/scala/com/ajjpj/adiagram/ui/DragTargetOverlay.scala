package com.ajjpj.adiagram.ui

import javafx.scene.layout.Pane
import com.ajjpj.adiagram.model.{AShapeSpec, ABoxSpec, ADiagram}
import javafx.scene.shape.Circle
import javafx.scene.paint.Color


/**
 * @author arno
 */
class DragTargetOverlay(diagram: ADiagram) extends Pane {
  private var _activeItem: Option[AShapeSpec] = None

  def activeItem = _activeItem

  val boxes = diagram.elements.flatMap(_ match {
    case box: ABoxSpec => Some(box)
    case _ => None
  })

  private val markers = boxes.map(marker)
  markers.foreach(getChildren.add)

  //TODO make configurable
  val passiveColor = Color.color(.5, 0, 0, .4)
  val activeColor = Color.color(.5, 0, 0, 1)

  def onMouseMoved(p: AScreenPos) {
    _activeItem = None

    markers.foreach(n => {
      if(p.containedByNode(n)) {
        _activeItem = Some(n.spec)
        n.setFill(activeColor)
      }
      else {
        n.setFill(passiveColor)
      }
    })
  }

  private def marker(box: ABoxSpec) = new Marker(box)

  private class Marker(val spec: AShapeSpec) extends Circle {
    setCenterX(spec.boundsForResizing.center.x) //TODO make this into bindings? Currently not necessary but worth remembering?
    setCenterY(spec.boundsForResizing.center.y)

    setRadius(10) //TODO make configurable
    setFill(passiveColor)
    setStroke(Color.RED)
    setStrokeWidth(2)
  }
}

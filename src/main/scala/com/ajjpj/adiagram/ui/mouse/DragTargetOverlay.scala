package com.ajjpj.adiagram.ui.mouse

import javafx.scene.shape.Circle
import javafx.scene.paint.Color
import com.ajjpj.adiagram.model.diagram.{AShapeSpec, ADiagram, ABoxSpec}
import com.ajjpj.adiagram.ui.{AScreenPos, Zoom}
import com.ajjpj.adiagram.ui.presentation.DiagramOverlay
import com.ajjpj.adiagram.ui.fw.SystemConfiguration


/**
 * @author arno
 */
private[mouse] class DragTargetOverlay(diagram: ADiagram, zoom: Zoom) extends DiagramOverlay {
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
    setCenterX(AScreenPos.fromModel(spec.boundsForResizing.center, zoom).x) //TODO make this into bindings? Currently not necessary but worth remembering?
    setCenterY(AScreenPos.fromModel(spec.boundsForResizing.center, zoom).y)

    setRadius(SystemConfiguration.boxDragTargetRadius)
    setFill(passiveColor)
    setStroke(Color.RED)
    setStrokeWidth(2)
  }
}

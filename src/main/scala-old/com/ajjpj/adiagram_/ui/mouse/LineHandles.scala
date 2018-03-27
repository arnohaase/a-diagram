package com.ajjpj.adiagram_.ui.mouse

import com.ajjpj.adiagram_.geometry.{Angle, APoint}
import com.ajjpj.adiagram_.ui.{ADiagramController, AScreenPos}
import com.ajjpj.adiagram_.ui.fw.{Unbindable, Digest, SystemConfiguration}
import javafx.scene.shape.Rectangle
import com.ajjpj.adiagram_.ui.presentation.{ZOrdered}
import javafx.scene.paint.Color


/**
 * @author arno
 */
private[mouse] class LineHandles(ctrl: ADiagramController)(implicit digest: Digest) extends Unbindable {
  private val lineStartHandle = new LineSelectionHandle()
  private val lineEndHandle = new LineSelectionHandle()

  private val handles = List(lineStartHandle, lineEndHandle)

  private def startPos = if(ctrl.selections.selectionIsSingleLine) ctrl.selections.singleSelectedLine.p0Source.pos else APoint.ZERO
  private def endPos   = if(ctrl.selections.selectionIsSingleLine) ctrl.selections.singleSelectedLine.p1Source.pos else APoint.ZERO
  private def endPointWithDistance(p0: APoint, p1: APoint): AScreenPos = AScreenPos.fromModel(p0 + (Angle.fromLine(p0, p1), -SystemConfiguration.distanceOfHandlesFromShapes / ctrl.zoom.factor), ctrl.zoom)

  digest.bindDouble(lineStartHandle.xProperty(), endPointWithDistance(startPos, endPos).x - SystemConfiguration.dragHandleSize/2)
  digest.bindDouble(lineStartHandle.yProperty(), endPointWithDistance(startPos, endPos).y - SystemConfiguration.dragHandleSize/2)
  digest.bindDouble(lineEndHandle.  xProperty(), endPointWithDistance(endPos, startPos).x - SystemConfiguration.dragHandleSize/2)
  digest.bindDouble(lineEndHandle.  yProperty(), endPointWithDistance(endPos, startPos).y - SystemConfiguration.dragHandleSize/2)

  handles.foreach(_ match {
    case l: LineSelectionHandle => digest.bindBoolean(l.visibleProperty, ctrl.selections.selectionIsSingleLine)
  })

  handles.foreach(ctrl.root.getChildren.add)

  /**
   * @return true -> start, false -> end
   */
  def lineEndFor(p: AScreenPos): Option[Boolean] = handles.find(h => h.isVisible && h.contains(p.x, p.y)).map(_ == lineStartHandle)

  override def unbind()(implicit digest: Digest) {
    handles.foreach(h => {
      digest.unbind(h.xProperty)
      digest.unbind(h.yProperty)
      digest.unbind(h.visibleProperty)
      ctrl.root.getChildren.remove(h)
    })
  }

  private class LineSelectionHandle extends Rectangle with ZOrdered {
    setFill(Color.BLACK)
    setWidth(SystemConfiguration.dragHandleSize)
    setHeight(SystemConfiguration.dragHandleSize)

    override def z = Integer.MAX_VALUE
  }
}

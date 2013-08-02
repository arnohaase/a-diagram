package com.ajjpj.adiagram.model

import com.ajjpj.adiagram.geometry.{ARect, ADim, APoint}
import com.ajjpj.adiagram.render.shapes.ALineShape
import com.ajjpj.adiagram.render.base.{TextStyle, LineStyle}
import com.ajjpj.adiagram.render.shapes.lineend.{RoundPointedArrowLineEnd, RoundedCornerLineEnd}
import javafx.beans.property.SimpleObjectProperty
import com.ajjpj.adiagram.ui.fw.Digest
import javafx.beans.value.{ObservableValue, ChangeListener}

/**
 * @author arno
 */
class ALineSpec(var p0: APoint, var p1: APoint, var text: Option[String], lineStyle: LineStyle, textStyle: TextStyle)(implicit digest: Digest) extends AShapeSpec {
  protected override def shape = new ALineShape(p0, p1, lineStyle, textStyle, new RoundedCornerLineEnd(.5), new RoundPointedArrowLineEnd(), text) //TODO configurable line ends

  private val p0Binding = new BindableLineEnd(p0)
  private val p1Binding = new BindableLineEnd(p1)

  p0Binding.opposite = () => p1Binding.unclippedValue
  p1Binding.opposite = () => p0Binding.unclippedValue

  private def refreshPos() {
    p0 = p0Binding.value
    p1 = p1Binding.value
  }

  def bindStartPoint(box: ABoxSpec): Unit = p0Binding.bind(box)
  def bindStartPoint(p: => APoint, clipBounds: => ARect): Unit = p0Binding.bind(p, clipBounds)
  def unbindStartPoint() = p0Binding.unbind()

  def bindEndPoint(box: ABoxSpec): Unit = p1Binding.bind(box)
  def bindEndPoint(p: => APoint, clipBounds: => ARect): Unit = p1Binding.bind(p, clipBounds)
  def unbindEndPoint() = p1Binding.unbind()

  override def boundsForResizing = ARect(p0, p1)
  protected def pos = boundsForResizing.topLeft

  def resizeBy(delta: ADim) {
    if(p0.x > p1.x) {
      p0 = p0.copy(x = p0.x + delta.width)
    }
    else {
      p1 = p1.copy(x = p1.x + delta.width)
    }

    if(p0.y > p1.y) {
      p0 = p0.copy(y = p0.y + delta.height)
    }
    else {
      p1 = p1.copy(y = p1.y + delta.height)
    }
  }

  protected def doMoveBy(delta: APoint) {
    if(delta != APoint.ZERO) {
//      digest.undoRedo.push(new BindLineEndsCommand(startPointBinding, None, endPointBinding, None))
      unbindStartPoint()
      unbindEndPoint()
    }

    p0 += delta
    p1 += delta
  }

  private[ALineSpec] case class PointAndClipRect (point: APoint, clipBounds: ARect)

  private class BindableLineEnd(defaultValue: => APoint) {
    var opposite: () => APoint = _

    private val prop = new SimpleObjectProperty[PointAndClipRect]()
    prop.addListener(new ChangeListener[PointAndClipRect] {
      def changed(o: ObservableValue[_ <: PointAndClipRect], oldValue: PointAndClipRect, newValue: PointAndClipRect) {
        atomicUpdate { refreshPos() }
      }
    })

    private var point:      Option[() => APoint] = None
    private var clipBounds: Option[() => ARect]  = None

    def unclippedValue = if(isBound) prop.getValue.point else defaultValue
    def value = if(isBound) prop.getValue.clipBounds.intersection(prop.getValue.point, opposite()) else  defaultValue

    def isBound = point.isDefined

    def bind(box: ABoxSpec): Unit = bind(ARect(box.pos, box.dim).center, ARect(box.pos, box.dim))
    def bind(p: => APoint, clipBounds: => ARect) {
      unbind()
      point = Some (() => p)
      this.clipBounds = Some(() => clipBounds)
      digest.bind(prop, PointAndClipRect(p, clipBounds))
    }

    def unbind() = {
      if(isBound) {
        digest.unbind(prop)
        point = None
        clipBounds = None
      }
    }
  }

}

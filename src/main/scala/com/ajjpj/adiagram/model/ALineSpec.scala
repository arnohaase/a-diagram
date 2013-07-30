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

  private val p0BindingProp = new SimpleObjectProperty[(APoint, ARect)]()
  private val p1BindingProp = new SimpleObjectProperty[(APoint, ARect)]()

  p0BindingProp.addListener(new ChangeListener[(APoint, ARect)]() {
    def changed(o: ObservableValue[_ <: (APoint, ARect)], oldValue: (APoint, ARect), newValue: (APoint, ARect)) {
      if(oldValue != newValue) {
        atomicUpdate {
          p0 = newValue._1 //TODO clipping
        }
      }
    }
  })
  p1BindingProp.addListener(new ChangeListener[(APoint, ARect)]() {
    def changed(o: ObservableValue[_ <: (APoint, ARect)], oldValue: (APoint, ARect), newValue: (APoint, ARect)) {
      if(oldValue != newValue) {
        atomicUpdate {
          p1 = newValue._1 //TODO clipping
        }
      }
    }
  })

  def bindStartPoint(box: ABoxSpec): Unit = bindStartPoint(ARect(box.pos, box.dim).center, ARect(box.pos, box.dim))
  def bindStartPoint(p: => APoint, clipBounds: => ARect): Unit = {
    digest.unbind(p0BindingProp)
    digest.bind(p0BindingProp, (p, clipBounds))
  }
  def unbindStartPoint()(implicit digest: Digest) = digest.unbind(p0BindingProp)

  def bindEndPoint(box: ABoxSpec): Unit = bindEndPoint(ARect(box.pos, box.dim).center, ARect(box.pos, box.dim))
  def bindEndPoint(p: => APoint, clipBounds: => ARect) {
    digest.unbind(p1BindingProp)
    digest.bind(p1BindingProp, (p, clipBounds))
  }
  def unbindEndPoint()(implicit digest: Digest) = digest.unbind(p0BindingProp)

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
    p0 += delta
    p1 += delta
  }
}

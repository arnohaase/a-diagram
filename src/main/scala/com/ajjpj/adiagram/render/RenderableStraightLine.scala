package com.ajjpj.adiagram.render

import java.util.concurrent.atomic.AtomicLong
import javafx.scene.canvas.GraphicsContext
import javafx.scene.shape.StrokeLineCap

import com.ajjpj.adiagram.geometry.{Angle, Length, RectShape, Vector2}
import com.ajjpj.adiagram.render.lineend.RenderableLineEnd


//TODO text
case class RenderableStraightLine(p0: Vector2, p1: Vector2, style: LineStyle, startDecoration: RenderableLineEnd, endDecoration: RenderableLineEnd) extends Renderable {
  override val pos = RectShape(p0, p1).topLeft

  override def renderBounds(m2s: Model2Screen) = {
    val decorationWidth = Length.max(startDecoration.width(style, m2s), endDecoration.width(style, m2s))
//    text match {
//      case Some(_) => decorationWidth = Math.max(decorationWidth, style.widthNoZoom + textStyle.height(Zoom.Identity)*2)
//      case None =>
//    }
    val padding = Length.max(style.width, decorationWidth) / 2
    RectShape.createWithPadding(p0, p1, padding)
  }

  val angle = Angle.fromLine(p0, p1)

  override def render(m2s: Model2Screen) = {
    val t0 = System.currentTimeMillis()
    val t1 = new AtomicLong()
    val t2 = new AtomicLong()

    val result = RenderedItem.fromGc(pos, renderBounds(m2s), m2s, None, (gc, t) => {
      t1.set(System.currentTimeMillis())
      paint(gc, t, m2s)
      t2.set(System.currentTimeMillis())
    })

    val t3 = System.currentTimeMillis()

    println(s"render time: ${t1.get - t0}ms / ${t2.get - t1.get}ms / ${t3 - t2.get}ms")

    result
  }

  def paint(gc: GraphicsContext, t: Vector2, m2s: Model2Screen) {
    style.applyTo(gc, m2s)
    startDecoration.paint(gc, p0, angle,          style, t, m2s)
    style.applyTo(gc, m2s)
    endDecoration  .paint(gc, p1, angle.opposite, style, t, m2s)

    paintCore(gc, t, m2s)

//    text match {
//      case Some(txt) => paintText(gc, t, txt, zoom)
//      case None =>
//    }
  }

  private def paintCore(gc: GraphicsContext, t: Vector2, m2s: Model2Screen) {
    style.applyTo(gc, m2s)
    gc.setLineCap(StrokeLineCap.BUTT)

    val start = m2s.toScreenCoordinates(p0 + Vector2.fromAngle(angle,          startDecoration.shortenLength(style, m2s)), t)
    val end   = m2s.toScreenCoordinates(p1 + Vector2.fromAngle(angle.opposite, endDecoration.shortenLength(style, m2s)), t)

    gc.strokeLine(start.x, start.y, end.x, end.y)
  }

//  private def paintText(gc: GraphicsContext, t: Translation, txt: String, zoom: Zoom) {
//    val mid: APoint = t(p0 halfWayTo p1)
//    val ccw = angle.angle <= Math.PI
//
//    val angleUp = if(ccw) angle.ccw90 else angle.cw90
//    val textBasePoint = AScreenPos.fromModel (mid + (angleUp, style.widthNoZoom), zoom)
//
//    val rotateAngle = if(ccw) -angle.screenDegrees else 180 - angle.screenDegrees
//    val rotate: Rotate = Transform.rotate(rotateAngle, textBasePoint.x, textBasePoint.y)
//    gc.setTransform(rotate.getMxx(), rotate.getMyx(), rotate.getMxy(), rotate.getMyy(), rotate.getTx(), rotate.getTy());
//
//    textStyle.applyTo(gc, zoom)
//
//    gc.setTextBaseline(VPos.BOTTOM)
//    gc.setTextAlign(TextAlignment.CENTER)
//
//    gc.fillText(txt, textBasePoint.x, textBasePoint.y)
//  }

}

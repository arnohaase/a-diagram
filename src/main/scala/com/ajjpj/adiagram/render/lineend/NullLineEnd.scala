package com.ajjpj.adiagram.render.lineend

import javafx.scene.canvas.GraphicsContext

import com.ajjpj.adiagram.geometry.{Angle, Length, Vector2}
import com.ajjpj.adiagram.render.{LineStyle, Model2Screen}


class NullLineEnd extends RenderableLineEnd {
  override def shortenLength(style: LineStyle, m2s: Model2Screen) = Length.ZERO
  override def width(style: LineStyle, m2s: Model2Screen) = Length.ZERO

  override def paint(gc: GraphicsContext, p: Vector2, angle: Angle, style: LineStyle, t: Vector2, m2s: Model2Screen) {
  }
}

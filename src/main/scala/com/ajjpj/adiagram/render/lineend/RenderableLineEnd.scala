package com.ajjpj.adiagram.render.lineend

import javafx.scene.canvas.GraphicsContext

import com.ajjpj.adiagram.geometry.{Angle, Length, Vector2}
import com.ajjpj.adiagram.render.{LineStyle, Model2Screen}


trait RenderableLineEnd {
  def shortenLength(style: LineStyle, m2s: Model2Screen): Length
  def width(style: LineStyle, m2s: Model2Screen): Length

  /**
   * @param p is the end of the physical line *including* the line ending (i.e. before
   *          being corrected with the result of getShortenLength())
   * @param angle direction of the line as if it was *starting* from this end
   */
  def paint(gc: GraphicsContext, p: Vector2, angle: Angle, style: LineStyle, t: Vector2, ptToDbl: Model2Screen): Unit
}

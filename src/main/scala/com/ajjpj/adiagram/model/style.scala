package com.ajjpj.adiagram.model

import javafx.scene.paint._
import com.ajjpj.adiagram.render.base.{TextStyle, LineStyle, ShadowStyle, FillStyle}
import javafx.scene.effect.BlurType
import javafx.geometry.VPos
import javafx.scene.text.TextAlignment


/**
 * @author arno
 */
class ColorSpec {
  def this(name: String, color: Color) = {this(); this.name = name; this.color = color}
  var name: String = _
  var color: Color = _
}


trait FillStyleSpec {
  def name: String
  def style = new FillStyle(paint)
  def paint: Paint
}

class SolidFillSpec extends FillStyleSpec {
  override def name = "Solid " + colorSpec.name
  var colorSpec: ColorSpec = _
  override def paint = colorSpec.color
}

class SimpleLinearGradientSpec extends FillStyleSpec {
  var colorSpec0: ColorSpec = _
  var colorSpec1: ColorSpec = _

  override def name = "Linear " + colorSpec0.name + " to " + colorSpec1.name
  override def paint = new LinearGradient(0.3, 0, .7, 1, true, CycleMethod.NO_CYCLE, new Stop(0, colorSpec0.color), new Stop(1, colorSpec1.color))
}


trait ShadowStyleSpec {
  def name: String
  def style: ShadowStyle
}

class SimpleShadowSpec extends ShadowStyleSpec {
  override def name = "Simple Shadow"
  override def style = ShadowStyle(6, 6, 16, BlurType.GAUSSIAN, Color.color(.5, .5, .5))
}


class LineStyleSpec {
  def this(colorSpec: ColorSpec, width: Double) = {this(); this.colorSpec = colorSpec; this.width = width}
  var colorSpec: ColorSpec = _
  var width: Double = _

  def name = "Width " + width + ", " + colorSpec.name
  def style = LineStyle(colorSpec.color, width)
}


trait TextStyleSpec {
  def name: String
  def style: TextStyle
}

class SimpleBoxTextSpec extends TextStyleSpec {
  override def name = "Simple Box Text"
  override def style = TextStyle(72, TextAlignment.CENTER, VPos.CENTER)
}

class SimpleLineTextSpec extends TextStyleSpec {
  override def name = "Simple Line Text"
  override def style = TextStyle(30, TextAlignment.CENTER, VPos.CENTER)
}

package com.ajjpj.adiagram.model

import com.ajjpj.adiagram.render.base.{LineStyle, TextStyle, ShadowStyle, FillStyle}
import javafx.scene.paint.{Color, Stop, CycleMethod, LinearGradient}
import javafx.scene.effect.BlurType
import javafx.scene.text.TextAlignment
import javafx.geometry.VPos
import com.ajjpj.adiagram.model.style.{ShadowStyleSpec, TextStyleSpec, LineStyleSpec, FillStyleSpec}

/**
 * @author arno
 */
class SelectedStyles {
  var fillStyle: FillStyleSpec = null //new FillStyleSpec(new LinearGradient(0.3, 0, .7, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.LIGHTBLUE), new Stop(1, Color.AZURE)))
  var shadowStyle: ShadowStyleSpec = null //new ShadowStyle(6, 6, 16, BlurType.GAUSSIAN, Color.color(.5, .5, .5))
  var textStyle: TextStyleSpec = null //new TextStyle(72, TextAlignment.CENTER, VPos.CENTER)
  var lineStyle: LineStyleSpec = null //new LineStyle(Color.BLACK, 5)
  var lineTextStyle: TextStyleSpec = null //new TextStyle(30, TextAlignment.CENTER, VPos.CENTER)
}

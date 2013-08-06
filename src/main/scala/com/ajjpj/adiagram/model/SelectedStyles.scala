package com.ajjpj.adiagram.model

import com.ajjpj.adiagram.render.base.{LineStyle, TextStyle, ShadowStyle, FillStyle}
import javafx.scene.paint.{Color, Stop, CycleMethod, LinearGradient}
import javafx.scene.effect.BlurType
import javafx.scene.text.TextAlignment
import javafx.geometry.VPos

/**
 * @author arno
 */
class SelectedStyles {
  var fillStyle = new FillStyle(new LinearGradient(0.3, 0, .7, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.LIGHTBLUE), new Stop(1, Color.AZURE)))
  var shadowStyle = new ShadowStyle(6, 6, 16, BlurType.GAUSSIAN, Color.color(.5, .5, .5))
  var textStyle = new TextStyle(72, TextAlignment.CENTER, VPos.CENTER)
  var lineStyle = new LineStyle(Color.BLACK, 5)
  var lineTextStyle = new TextStyle(30, TextAlignment.CENTER, VPos.CENTER)
}

package com.ajjpj.adiagram_.model

import com.ajjpj.adiagram_.render.base.{LineStyle, TextStyle, ShadowStyle, FillStyle}
import javafx.scene.paint.{Color, Stop, CycleMethod, LinearGradient}
import javafx.scene.effect.BlurType
import javafx.scene.text.TextAlignment
import javafx.geometry.VPos
import com.ajjpj.adiagram_.model.style._

/**
 * @author arno
 */
class SelectedStyles {
  var fillStyle: FillStyleSpec = null
  var shadowStyle: ShadowStyleSpec = null
  var textStyle: TextStyleSpec = null
  var lineStyle: LineStyleSpec = null
  var lineTextStyle: TextStyleSpec = null

  var startLineEnd: LineEndSpec = RoundedCornerLineEndSpec
  var endLineEnd: LineEndSpec = RoundPointedArrowLineEndSpec
}

object SelectedStyles {
  def createFromDefaultRepo(styleRepository: AStyleRepository) = {
    val result = new SelectedStyles

    result.fillStyle     = styleRepository.fillStyles.iterator.next() //    new FillStyle(new LinearGradient(0.3, 0, .7, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.LIGHTBLUE), new Stop(1, Color.AZURE)))
    result.shadowStyle   = styleRepository.shadowStyles.iterator.next // = new ShadowStyle(6, 6, 16, BlurType.GAUSSIAN, Color.color(.5, .5, .5))
    result.textStyle     = styleRepository.textStyles.find(_.name == "Box").get
    result.lineTextStyle = styleRepository.textStyles.find(_.name == "Line").get
    result.lineStyle     = styleRepository.lineStyles.find(_.width > 3.5).get

    result
  }
}
package com.ajjpj.adiagram.model.style

import com.ajjpj.adiagram.render.shapes.lineend._


/**
 * @author arno
 */
sealed abstract class LineEndSpec(val name: String, val id: Int) {
  def lineEnd: ALineEnd
}


object CutOffLineEndSpec extends LineEndSpec("Cut Off", 1) {
  override val lineEnd = new NullLineEnd
}

object RoundedCornerLineEndSpec extends LineEndSpec("Rounded Corner", 2) {
  override val lineEnd = new RoundedCornerLineEnd(.5)
}

object SemiCircleLineEndSpec extends LineEndSpec("Semi Circle", 3) {
  override val lineEnd = new SemiCircleLineEnd
}
object PointedArrowLineEndSpec extends LineEndSpec("Pointed Arrow", 4) {
  override val lineEnd = new PointedArrowLineEnd
}

object RoundPointedArrowLineEndSpec extends LineEndSpec("Round Pointed Arrow", 6) {
  override val lineEnd = new RoundPointedArrowLineEnd
}

object RoundArrowLineEndSpec extends LineEndSpec("Round Arrow", 5) {
  override val lineEnd = new RoundArrowLineEnd
}



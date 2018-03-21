package com.ajjpj.adiagram_.model.style

import com.ajjpj.adiagram_.render.shapes.lineend._
import java.util.UUID


/**
 * @author arno
 */
sealed abstract class LineEndSpec(val name: String, val uuid: UUID) {
  def lineEnd: ALineEnd
}
object CutOffLineEndSpec extends LineEndSpec("Cut Off", UUID.fromString("08ea16c4-d28b-41b2-8d4a-5227a442ab60")) {
  override val lineEnd = new NullLineEnd
}

object RoundedCornerLineEndSpec extends LineEndSpec("Rounded Corner", UUID.fromString("953e2b87-8316-4f9d-a772-55e6ea7dd487")) {
  override val lineEnd = new RoundedCornerLineEnd(.5)
}

object SemiCircleLineEndSpec extends LineEndSpec("Semi Circle", UUID.fromString("b72ab811-2c91-4219-a15d-66edaca873fd")) {
  override val lineEnd = new SemiCircleLineEnd
}
object PointedArrowLineEndSpec extends LineEndSpec("Pointed Arrow", UUID.fromString("d5d0589a-4fb1-4b48-8a46-121ba2ab0833")) {
  override val lineEnd = new PointedArrowLineEnd
}

object RoundPointedArrowLineEndSpec extends LineEndSpec("Round Pointed Arrow", UUID.fromString("60bbeefe-7673-4538-bcbf-aa131b23358d")) {
  override val lineEnd = new RoundPointedArrowLineEnd
}

object RoundArrowLineEndSpec extends LineEndSpec("Round Arrow", UUID.fromString("c3acac10-35d4-489e-9d2c-457550809dd8")) {
  override val lineEnd = new RoundArrowLineEnd
}

object FilledTriangleLineEndSpec extends LineEndSpec("Empty Triangle",  UUID.fromString("178b9425-8903-4d71-aaa4-937b27865564")) {
	override val lineEnd = new FilledTriangleLineEnd
}



package com.ajjpj.adiagram.model.style

import javafx.scene.paint.Color


/**
 * @author arno
 */
class AStyleRepository {
  var colors = Set[ColorSpec]()
  var fillStyles = Set[FillStyleSpec]()
  var shadowStyles = Set(SimpleShadowSpec, NoShadowSpec)
  var lineStyles = Set[LineStyleSpec]()
  var textStyles = Set[TextStyleSpec]()

  val lineEnds = List(CutOffLineEndSpec, RoundedCornerLineEndSpec, SemiCircleLineEndSpec, PointedArrowLineEndSpec, RoundPointedArrowLineEndSpec, RoundArrowLineEndSpec, FilledTriangleLineEndSpec)
}

object AStyleRepository {
  def default = {
    val repo = new AStyleRepository()

    val black     = new ColorSpec("black",      Color.BLACK)
    val red       = new ColorSpec("red",        Color.RED)
    val green     = new ColorSpec("green",      Color.GREEN)
    val lightBlue = new ColorSpec("light blue", Color.LIGHTBLUE)
    val azure     = new ColorSpec("azure",      Color.AZURE)

    repo.colors = Set(black, red, green, lightBlue, azure)
    repo.fillStyles = Set(new SimpleLinearGradientSpec(lightBlue, azure), new SimpleLinearGradientSpec(red, green))
    repo.lineStyles = Set(new LineStyleSpec(black, 1), new LineStyleSpec(black, 2), new LineStyleSpec(black, 4))
    repo.textStyles = Set(new SimpleTextStyleSpec("Box", 72), new SimpleTextStyleSpec("Line", 30))

    repo
  }
}

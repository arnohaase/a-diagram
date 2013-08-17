package com.ajjpj.adiagram.model.style

import javafx.scene.paint.Color


/**
 * @author arno
 */
class AStyleRepository {
  /**
   * This is a compromise solution to allow cheap 'watching' for style repo changes. It does however require *all* code making
   *  changes to the repo to increment this counter so that dependent code is notified of the change. This is based on the assumption
   *  that repo changes are pretty rare and therefore simplicity takes precedence over fine-grained change notifications. Or put
   *  differently: repo changes are so rare that unnecessary updates - even somewhat expensive ones - are deemed an acceptable trade-off.
   */
  var changeCounter = 0

  var colors = List[ColorSpec]()
  var fillStyles = List[FillStyleSpec]()
  var shadowStyles = List(SimpleShadowSpec, NoShadowSpec)
  var lineStyles = List[LineStyleSpec]()
  var textStyles = List[TextStyleSpec]()

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

    repo.colors = List(black, red, green, lightBlue, azure)
    repo.fillStyles = List(new SimpleLinearGradientSpec(lightBlue, azure), new SimpleLinearGradientSpec(red, green))
    repo.lineStyles = List(new LineStyleSpec(black, 1), new LineStyleSpec(black, 2), new LineStyleSpec(black, 4))
    repo.textStyles = List(new SimpleTextStyleSpec("Box", 72), new SimpleTextStyleSpec("Line", 30))

    repo
  }
}

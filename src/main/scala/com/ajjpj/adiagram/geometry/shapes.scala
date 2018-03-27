package com.ajjpj.adiagram.geometry


trait GeometricShape {
  def contains(p: Vector2): Boolean
  def intersection(inside: Vector2, outside: Vector2): Vector2
}

case class RectShape(topLeft: Vector2, _bottomRight: Vector2) extends GeometricShape {
  def unit = topLeft.unit
  val bottomRight = _bottomRight.inUnit(unit)
  val dim = Vector2(bottomRight.x - topLeft.x, bottomRight.y - topLeft.y, unit)

  def topRight    = topLeft + Vector2(dim.x, 0, unit)
  def bottomLeft  = topLeft + Vector2(0, dim.y, unit)

  def width = Length(dim.x, unit)
  def height = Length(dim.y, unit)

  def top = Length(topLeft.y, unit)
  def bottom = Length(bottomLeft.y, unit)
  def left = Length(topLeft.x, unit)
  def right = Length(topRight.x, unit)

  def center = topLeft halfWayTo bottomRight

  def withPadding(padding: Length): RectShape = withPadding(padding, padding)
  def withPadding(hPadding: Length, vPadding: Length): RectShape = withPadding(hPadding, vPadding, hPadding, vPadding)
  def withPadding(leftPadding: Length, topPadding: Length, rightPadding: Length, bottomPadding: Length): RectShape = {
    val l = leftPadding.inUnit(unit)
    val r = rightPadding.inUnit(unit)
    val t = topPadding.inUnit(unit)
    val b = bottomPadding.inUnit(unit)
    RectShape(topLeft - Vector2(l.l, t.l, unit), Vector2(l.l + r.l, t.l + b.l, unit))
  }

  override def contains(p: Vector2) = {
    val pNorm = p.inUnit(unit)
    topLeft.x <= pNorm.x && topLeft.x+dim.x >= pNorm.x &&
      topLeft.y <= pNorm.y && topLeft.y+dim.y >= pNorm.y
  }

  def intersection(_inside: Vector2, _outside: Vector2): Vector2 = {
    val inside = _inside.inUnit(unit)
    val outside = _outside.inUnit(unit)

    val delta = outside - inside

    def vert = if (outside.y < top.l) {
      val candidateX = inside.x + delta.x * (top.l - inside.y) / delta.y
      if (candidateX >= left.l && candidateX <= right.l) Some(Vector2(candidateX, top.l, unit)) else None
    }
    else if (outside.y > bottom.l) {
      val candidateX = inside.x + delta.x * (inside.y-bottom.l) / delta.y
      if (candidateX >= left.l && candidateX <= right.l) Some(Vector2(candidateX, bottom.l, unit)) else None
    }
    else
      None

    def horiz = if (outside.x < left.l) {
      val candidateY = inside.y + delta.y * (inside.y - left.l) / delta.x
      if (candidateY >= top.l && candidateY <= bottom.l) Some(Vector2(left.l, candidateY, unit)) else None
    }
    else if (outside.x > right.l) {
      val candidateY = inside.y + delta.y * (right.l - inside.y) / delta.x
      if (candidateY >= top.l && candidateY <= bottom.l) Some(Vector2(right.l, candidateY, unit)) else None
    }
    else
      None

    (vert orElse horiz) getOrElse inside
  }
}

object RectShape {
  def fromDim(topLeft: Vector2, dim: Vector2): RectShape = RectShape(topLeft, topLeft + dim)

//  def apply(p0: Vector2, p1: Vector2): RectShape = createWithPadding(0, p0, p1)

//  def fromCoordinates(x0: Double, y0: Double, x1: Double, y1: Double) = apply(Vector2(x0, y0), Vector2(x1, y1))

  def createWithPadding(p0: Vector2, p1: Vector2, _padding: Length): RectShape = {
    val unit = p0.unit
    val x0 = p0.x
    val y0 = p0.y
    val x1 = p1.unit.convertTo(p1.x, unit)
    val y1 = p1.unit.convertTo(p1.y, unit)
    val padding = _padding.inUnit(unit).l

    val minX = Math.min(x0, x1) - padding
    val maxX = Math.max(x0, x1) + padding
    val minY = Math.min(y0, y1) - padding
    val maxY = Math.max(y0, y1) + padding

    new RectShape(Vector2(minX, minY, unit), Vector2(maxX, maxY, unit))
  }

  //TODO move to GeometricShape; 'enclosingRect' for each shape
//  def containingRect(rects: Iterable[RectShape]) = {
//    if(rects.isEmpty) {
//      RectShape(Vector2.ZERO, 0, 0)
//    }
//    else {
//      var minX = Double.MaxValue
//      var maxX = Double.MinValue
//      var minY = Double.MaxValue
//      var maxY = Double.MinValue
//
//      rects.foreach(r => {
//        minX = Math.min(minX, r.topLeft.x)
//        maxX = Math.max(maxX, r.bottomRight.x)
//        minY = Math.min(minY, r.topLeft.y)
//        maxY = Math.max(maxY, r.bottomRight.y)
//      })
//
//      fromCoordinates(minX, minY, maxX, maxY)
//    }
//  }
}

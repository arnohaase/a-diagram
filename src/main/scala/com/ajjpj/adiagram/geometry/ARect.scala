package com.ajjpj.adiagram.geometry


/**
 * @author arno
 */
case class ARect(topLeft: APoint, dim: ADim) extends GeometricShape {
  def topRight    = topLeft + ((dim.width, .0))
  def bottomLeft  = topLeft + ((.0, dim.height))
  def bottomRight = topLeft + ((dim.width, dim.height))

  def width = dim.width
  def height = dim.height

  def center = topLeft halfWayTo bottomRight

  def withPadding(padding: Double) = ARect(topLeft - ((padding, padding)), ADim(dim.width + 2*padding, dim.height + 2*padding))

  override def contains(p: APoint) =
    topLeft.x <= p.x && topLeft.x+dim.width  >= p.x &&
    topLeft.y <= p.y && topLeft.y+dim.height >= p.y

  override def toString = "ARect{" + topLeft + ", dim " + dim + "}"

  private def segmentIntersectionVertical(p1: APoint, p2: APoint, x: Double, y3: Double, y4: Double): Option[APoint] = {
    if(x < Math.min(p1.x, p2.x) || x > Math.max(p1.x, p2.x)) {
      None
    }
    else {
      val det = (p1.x - p2.x)*(y3 - y4)
      if (det == 0.0) {
        None
      }
      else {
        val y_res = ((p1.x*p2.y - p1.y*p2.x)*(y3 - y4) - x*(p1.y - p2.y)*(y4 - y3)) / det

        if(y_res >= Math.min(y3, y4) && y_res <= Math.max(y3, y4))
          Some(APoint(x, y_res))
        else
          None
      }
    }
  }

  private def segmentIntersectionHorizontal(p1: APoint, p2: APoint, x3: Double, x4: Double, y: Double): Option[APoint] = {
    if(y < Math.min(p1.y, p2.y) || y > Math.max(p1.y, p2.y)) {
      None
    }
    else {
      val det = -(x3 - x4)*(p1.y - p2.y)
      if (det == 0.0) {
        None
      }
      else {
        val x_res = ((p1.x*p2.y - p1.y*p2.x)*(x3 - x4) - y*(p1.x - p2.x)*(x3 - x4)) / det

        if(x_res >= Math.min(x3, x4) && x_res <= Math.max(x3, x4))
          Some(APoint(x_res, y))
        else
          None
      }
    }
  }

  def intersection(inside: APoint, outside: APoint) = {
    val result =
    segmentIntersectionHorizontal (inside, outside, topLeft.x,         topLeft.x + width, topLeft.y).getOrElse (
    segmentIntersectionHorizontal (inside, outside, topLeft.x,         topLeft.x + width, topLeft.y + height).getOrElse (
    segmentIntersectionVertical   (inside, outside, topLeft.x,         topLeft.y        , topLeft.y + height).getOrElse (
    segmentIntersectionVertical   (inside, outside, topLeft.x + width, topLeft.y        , topLeft.y + height).get
    )))

    result
  }
}

object ARect {
  def apply(p0: APoint, p1: APoint): ARect = createWithPadding(0, p0, p1)
  def apply(p: APoint, width: Double, height: Double): ARect = ARect(p, ADim(width, height))

  def fromCoordinates(x0: Double, y0: Double, x1: Double, y1: Double) = apply(APoint(x0, y0), APoint(x1, y1))

  def createWithPadding(padding: Double, p0: APoint, p1: APoint): ARect = {
    val minX = Math.min(p0.x, p1.x) - padding
    val maxX = Math.max(p0.x, p1.x) + padding
    val minY = Math.min(p0.y, p1.y) - padding
    val maxY = Math.max(p0.y, p1.y) + padding

    new ARect(APoint(minX, minY), ADim(maxX-minX, maxY-minY))
  }

  def containingRect(rects: Iterable[ARect]) = {
    if(rects.isEmpty) {
      throw new IllegalArgumentException()
    }

    var minX = Double.MaxValue
    var maxX = Double.MinValue
    var minY = Double.MaxValue
    var maxY = Double.MinValue

    rects.foreach(r => {
      minX = Math.min(minX, r.topLeft.x)
      maxX = Math.max(maxX, r.bottomRight.x)
      minY = Math.min(minY, r.topLeft.y)
      maxY = Math.max(minY, r.bottomRight.y)
    })

    fromCoordinates(minX, minY, maxX, maxY)
  }
}

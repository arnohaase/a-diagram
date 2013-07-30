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

  override def contains(p: APoint) =
    topLeft.x <= p.x && topLeft.x+dim.width  >= p.x &&
    topLeft.y <= p.y && topLeft.y+dim.height >= p.y

  override def toString = "ARect{" + topLeft + ", dim " + dim + "}"
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

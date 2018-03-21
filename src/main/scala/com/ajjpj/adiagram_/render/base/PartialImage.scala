package com.ajjpj.adiagram_.render.base

import com.ajjpj.adiagram_.geometry.{ARect, APoint}
import javafx.scene.image.Image
import javafx.scene.canvas.{Canvas, GraphicsContext}
import com.ajjpj.adiagram_.geometry.transform.Translation
import com.ajjpj.adiagram_.render.RenderHelper
import com.ajjpj.adiagram_.ui.fw.Digest
import com.ajjpj.adiagram_.ui.{Zoom, AScreenPos}


/**
 * This is the result of a rendering operation of a shape. To save memory (and improve cacheability), it does
 *  not contain the entire canvas but rather an arbitrary part of it plus a coordinate offset for rendering.
 *
 * @param renderOffset are the coordinates at which the upper left corner of the image should be rendered
 *
 * @author arno
 */
class PartialImage (val renderOffset: APoint, val img: Image)

class PartialImageWithShadow(val shape: PartialImage, val shadow: Option[PartialImage])

object PartialImage {
  type PartialImageRenderCallback = (GraphicsContext, Translation) => Unit

  /**
   * @param bounds of the actual image (toModel shadow, if any)
   * @param shadowStyle null if there is no shadow
   */
  def fromGc(pos: APoint, bounds: ARect, zoom: Zoom, shadowStyle: Option[ShadowStyle], callback: PartialImageRenderCallback): PartialImageWithShadow = {
    // one pixel on each side for sub-pixel bleeding
    val offset = bounds.topLeft + ((-1/zoom.factor, -1/zoom.factor))

    val canvas = new Canvas(bounds.width*zoom.factor+2, bounds.height*zoom.factor+2)
    val t = Translation(offset.inverse)
    callback(canvas.getGraphicsContext2D, t)

    val img = RenderHelper.snapshot(canvas)

    shadowStyle match {
      case Some(s) => new PartialImageWithShadow(new PartialImage(offset - pos, img), Some(new PartialImage(s.withShadowOffset(offset) - pos, s.shadow(img, zoom))))
      case None    => new PartialImageWithShadow(new PartialImage(offset - pos, img), None)
    }
  }
}



package com.ajjpj.adiagram.render.base

import com.ajjpj.adiagram.geometry.{ARect, APoint}
import javafx.scene.image.Image
import javafx.scene.canvas.{Canvas, GraphicsContext}
import com.ajjpj.adiagram.geometry.transform.Translation
import com.ajjpj.adiagram.render.RenderHelper
import com.ajjpj.adiagram.ui.fw.Digest


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
   * @param bounds of the actual image (without shadow, if any)
   * @param shadowStyle null if there is no shadow
   */
  def fromGc(pos: APoint, bounds: ARect, shadowStyle: Option[ShadowStyle], callback: PartialImageRenderCallback)(implicit digest: Digest): PartialImageWithShadow = {
    // one pixel on each side for sub-pixel bleeding
    val offset = bounds.topLeft + ((-1.0, -1.0))

    val canvas = new Canvas(bounds.width+2, bounds.height+2)
    val t = Translation(offset.inverse)
    callback(canvas.getGraphicsContext2D, t)

    val img = RenderHelper.snapshot(canvas)

    shadowStyle match {
      case Some(s) => new PartialImageWithShadow(new PartialImage(offset - pos, img), Some(new PartialImage(s.withShadowOffset(offset) - pos, s.shadow(img))))
      case None    => new PartialImageWithShadow(new PartialImage(offset - pos, img), None)
    }
  }
}



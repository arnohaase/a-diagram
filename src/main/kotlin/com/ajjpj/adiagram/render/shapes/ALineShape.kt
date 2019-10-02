package com.ajjpj.adiagram.render.shapes

import com.ajjpj.adiagram.Zoom
import com.ajjpj.adiagram.geometry.APoint
import com.ajjpj.adiagram.geometry.ARect
import com.ajjpj.adiagram.geometry.AffineTransformation
import com.ajjpj.adiagram.geometry.Angle
import com.ajjpj.adiagram.render.AScreenPos
import com.ajjpj.adiagram.render.PartialImage
import com.ajjpj.adiagram.render.shapes.lineend.ALineEnd
import com.ajjpj.adiagram.render.style.LineStyle
import com.ajjpj.adiagram.render.style.TextStyle
import javafx.geometry.VPos
import javafx.scene.canvas.GraphicsContext
import javafx.scene.shape.StrokeLineCap
import javafx.scene.text.TextAlignment
import javafx.scene.transform.Rotate
import javafx.scene.transform.Transform

data class ALineShape(val p0: APoint, val p1: APoint, val style: LineStyle, val textStyle: TextStyle, val startDecoration: ALineEnd, val endDecoration: ALineEnd, val text: String?): AShape {
    override val pos = ARect(p0, p1).topLeft

    override val bounds: ARect
        get() {
            var decorationWidth = Math.max(startDecoration.width(style, Zoom.Identity), endDecoration.width(style, Zoom.Identity))
            if (text != null) {
                decorationWidth = Math.max(decorationWidth, style.widthNoZoom + textStyle.height(Zoom.Identity)*2)
            }
            val padding = Math.max(style.widthNoZoom, decorationWidth) / 2

            return ARect.createWithPadding(padding, p0, p1)
        }

    override val renderBounds = bounds


    val angle = Angle.fromLine(p0, p1)

    override fun render(zoom: Zoom) = PartialImage.fromGc(pos, bounds, zoom, null, fun (gc: GraphicsContext, t: AffineTransformation) {
        paint(gc, t, zoom)
    })

    fun paint(gc: GraphicsContext, t: AffineTransformation, zoom: Zoom) {
        style.applyTo(gc)

        startDecoration.paint(gc, p0, angle,          style, t, zoom)
        endDecoration  .paint(gc, p1, angle.opposite, style, t, zoom)

        paintCore(gc, t, zoom)

        if (text != null) {
            paintText(gc, t, text, zoom)
        }
    }

    private fun paintCore(gc: GraphicsContext, t: AffineTransformation, zoom: Zoom) {
        gc.lineWidth = style.width(zoom)
        gc.lineCap = StrokeLineCap.BUTT

        val start = AScreenPos.fromModel(t(p0 + APoint(angle,          startDecoration.shortenLengthUnzoomed(style))), zoom)
        val end   = AScreenPos.fromModel(t(p1 + APoint(angle.opposite, endDecoration  .shortenLengthUnzoomed(style))), zoom)

        gc.strokeLine(start.x, start.y, end.x, end.y)
    }

    private fun paintText(gc: GraphicsContext, t: AffineTransformation, txt: String, zoom: Zoom) {
        val mid: APoint = t(p0.halfWayTo (p1))
        val ccw = angle.angle <= Math.PI

        val angleUp = if(ccw) angle.ccw90 else angle.cw90
        val textBasePoint = AScreenPos.fromModel (mid + APoint(angleUp, style.widthNoZoom), zoom)

        val rotateAngle = if(ccw) -angle.screenDegrees else 180 - angle.screenDegrees
        val rotate: Rotate = Transform.rotate(rotateAngle, textBasePoint.x, textBasePoint.y)
        gc.setTransform(rotate.getMxx(), rotate.getMyx(), rotate.getMxy(), rotate.getMyy(), rotate.getTx(), rotate.getTy())

        textStyle.applyTo(gc, zoom)

        gc.textBaseline = VPos.BOTTOM
        gc.textAlign = TextAlignment.CENTER

        gc.fillText(txt, textBasePoint.x, textBasePoint.y)
    }
}

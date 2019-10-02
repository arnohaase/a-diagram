package com.ajjpj.adiagram.render

import javafx.scene.Node
import javafx.scene.SnapshotParameters
import javafx.scene.paint.Color

class RenderHelper {
    companion object {
        val TransparentSnapshotParameters = snapshotParams(Color.TRANSPARENT)

        private fun snapshotParams(bgColor: Color): SnapshotParameters {
            val result = SnapshotParameters()
            result.fill = bgColor
            return result
        }

        fun snapshot(node: Node, bgColor: Color = Color.TRANSPARENT) = node.snapshot(snapshotParams(bgColor), null) // it is ok for None to trigger an exception - it was caused by one
    }
}
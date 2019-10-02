package com.ajjpj.adiagram.render.resources

import com.sun.javafx.tk.Toolkit
import javafx.scene.text.Font

class FontHelper {
    companion object {
        //TODO caching?
        fun actualHeightInPixels(f: Font) = Toolkit.getToolkit().fontLoader.getFontMetrics(f).lineHeight

        fun font(sizeInPixels: Double): Font {
            val raw = Font(sizeInPixels)
            val scaleFactor = actualHeightInPixels(raw) / sizeInPixels
            return Font(sizeInPixels / scaleFactor)
        }
    }
}
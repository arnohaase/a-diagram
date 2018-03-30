package com.ajjpj.adiagram.render.text

import javafx.scene.paint.Paint
import javafx.scene.text.{Font, FontPosture, FontWeight, TextAlignment}

import com.ajjpj.adiagram.geometry.Length
import com.ajjpj.adiagram.render.{Model2Screen, TextParagraphStyle}

import scala.collection.mutable.ArrayBuffer


/**
  * @param baselineY is in this line's coordinate system, i.e. y=0 is the top of the current line
  */
case class TypesetAtom(offsetX: Double, baselineY: Double, text: String, font: Font, fill: Paint, underline: Boolean, strikeThrough: Boolean)
case class LineOfTypesetText(offsetY: Double, height: Double, atoms: Vector[TypesetAtom])

/**
  * @param lines the Vector of typeset lines. All lines share a coordinate system with y=0 at the top and pointing down, and
  *         x=0 being on the left (hAlign = LEFT, JUSTIFY), center (CENTER) or right (RIGHT). Every line has an offsetY which
  *         its top coordinate in the complete text's coordinate system.
  */
case class TypesetText (lines: Vector[LineOfTypesetText], xMin: Double, xMax: Double, height: Double) {
  /**
    * NB: This may actually be *bigger* than wrapWidth if there is a word that is too long for a single line
    */
  def width = xMax - xMin
}

/**
  * NB: rotation is left to rendering
  */
class FixedWidthTypeSetter(_wrapWidth: Length, m2s: Model2Screen, textModel: TextModel) {
  import FixedWidthTypeSetter._

  val wrapWidth = m2s(_wrapWidth)

  // width and height in screen units
  private var xMin = 0.0
  private var xMax = 0.0

  /**
    * @return the Vector of typeset lines. All lines share a coordinate system with y=0 at the top and pointing down, and
    *         x=0 being on the left (hAlign = LEFT, JUSTIFY), center (CENTER) or right (RIGHT). Every line has an offsetY which
    *         its top coordinate in the complete text's coordinate system.
    */
  def build() = TypesetText(lines.toVector, xMin, xMax, offsetY)

  private var offsetY = 0.0
  private val lines = new ArrayBuffer[LineOfTypesetText]

  private var curLineMaxAscent = 0.0 // the amount of space the current line needs above its regular base line
  private var curLineMaxDescent = 0.0   // this is for *line* metrics --> descent below the line's 'regular' baseline
  private var curLineWidth = 0.0
  private var curLineAtoms = ArrayBuffer.empty[TypesetAtom]
  private def isStartOfLine = curLineAtoms.isEmpty

  for ((p, i) <- textModel.paragraphs.view.zipWithIndex) {
    val leadingVerticalGap = if(i==0) Length.ZERO else textModel.style.paragraphSpacing
    typesetParagraph(p, leadingVerticalGap)
  }

  private def typesetParagraph(p: TextParagraphModel, leadingVerticalGap: Length) {
    offsetY += m2s(leadingVerticalGap)
    for((atom, i) <- p.atoms.view.zipWithIndex) {
      typesetAtom(atom, p.style, i == p.atoms.length-1)
    }
  }

  private def typesetAtom(atom: TextAtomModel, paragraphStyle: TextParagraphStyle, isLastAtomInParagraph: Boolean) {
    def completeLine(isLastLineOfParagraph: Boolean) = {
      // Remove a single trailing blank, if any. This is important for RIGHT and JUSTIFY and nice to have for CENTER while not hurting LEFT --> just do it
      // We restrict ourselves to removing only onw blank to allow manual 'tweaking' of layout which is common practice
      if (curLineAtoms.size > 1 && curLineAtoms.last.text == " ") {
        // we avoid emptying a line completely by removing the (invisible) space
        curLineWidth = curLineAtoms.last.offsetX
        curLineAtoms.remove(curLineAtoms.size-1, 1)
      }
      val idxLastNonSpace = curLineAtoms.lastIndexWhere(_.text != " ")
      if (idxLastNonSpace >= 0 && idxLastNonSpace < curLineAtoms.size-1) {
        curLineWidth = curLineAtoms(idxLastNonSpace+1).offsetX
        curLineAtoms.remove (idxLastNonSpace + 1, curLineAtoms.size - idxLastNonSpace - 1)
      }

      // post process the line according to alignment
      paragraphStyle.hAlignment match {
        case TextAlignment.LEFT =>
          curLineAtoms = curLineAtoms.map(a => a.copy(baselineY = a.baselineY + curLineMaxAscent))
          xMax = Math.max(xMax, curLineWidth)
        case TextAlignment.CENTER =>
          curLineAtoms = curLineAtoms.map(a => a.copy(offsetX = a.offsetX - curLineWidth/2, baselineY = a.baselineY + curLineMaxAscent))
          xMax = Math.max(xMax, curLineWidth/2)
          xMin = Math.min(xMin, -curLineWidth/2)
        case TextAlignment.RIGHT =>
          curLineAtoms = curLineAtoms.map(a => a.copy(offsetX = a.offsetX - curLineWidth, baselineY = a.baselineY + curLineMaxAscent))
          xMin = Math.min(xMin, -curLineWidth)
        case TextAlignment.JUSTIFY =>
          // remove leading blank, if any, unless that empties the line completely
          val xCorrection = {
            if (curLineAtoms.size > 1 && curLineAtoms.head.text == " ") {
              curLineAtoms = curLineAtoms.tail //NB: side effect!
              curLineAtoms.head.offsetX
            }
            else
              0.0
          }

          // spread available space evenly
          val surplusSpace = {
            if (curLineWidth < wrapWidth && curLineAtoms.size > 1) {
              val result = (wrapWidth - curLineWidth) / (curLineAtoms.size - 1)
              curLineWidth = wrapWidth //NB: side effect!
              result
            }
            else
              0.0
          }

          val transformed = curLineAtoms.view.zipWithIndex.map(atomIdx => {
            val (atom, idx) = atomIdx
            atom.copy(offsetX = atom.offsetX + surplusSpace*idx - xCorrection, baselineY = atom.baselineY + curLineMaxAscent)
          }).toVector
          curLineAtoms.clear()
          curLineAtoms ++= transformed

          xMax = Math.max(xMax, curLineWidth) // always do this to deal with words too long for one line
          if (! isLastLineOfParagraph) xMax = Math.max(xMax, wrapWidth)
      }

      val newLine = LineOfTypesetText(offsetY, curLineMaxAscent + curLineMaxDescent, curLineAtoms.toVector)
      lines += newLine

      curLineAtoms.clear()
      curLineWidth = 0
      curLineMaxDescent = 0
      curLineMaxAscent = 0
      offsetY += newLine.height
      if (!isLastLineOfParagraph) offsetY += m2s(paragraphStyle.lineSpacing)
    }

    val weight = if (atom.style.bold) FontWeight.BOLD else FontWeight.NORMAL //TODO verify this
    val posture = if (atom.style.italics) FontPosture.ITALIC else FontPosture.REGULAR
    val font = Font.font(atom.style.fontFamily, weight, posture, m2s(atom.style.fontSize))
    val fm = new FontMetrics(font)

    val words = splitToWords(atom.text)

    for ((word, i) <- words.view.zipWithIndex) {
      val wordWidth = fm.computeStringWidth(word)
      if (curLineWidth + wordWidth > wrapWidth && !isStartOfLine) completeLine(false)

      //TODO superscript / subscript
      curLineAtoms += TypesetAtom(curLineWidth, 0, word, font, atom.style.fill, atom.style.underline, atom.style.strikeThrough)
      curLineMaxAscent = Math.max(curLineMaxAscent, fm.ascent)
      curLineMaxDescent = Math.max(curLineMaxDescent, fm.descent)
      curLineWidth += wordWidth
      if(isLastAtomInParagraph && i == words.length-1) completeLine(true)
    }
  }

  //TODO hyphenation, allow splits at '-'
  //TODO how best to represent strikethrough and underline?
}

object FixedWidthTypeSetter {
  def splitToWords(s: String) = {
    val builder = Vector.newBuilder[String]

    var startOfWord = -1
    for (i <- 0 until s.length) {
      s(i) match {
        case ' ' if startOfWord == -1 =>
          builder += " "
        case ' ' =>
          builder += s.substring(startOfWord, i)
          builder += " "
          startOfWord = -1
        case _ if startOfWord == -1 =>
          startOfWord = i
        case _ =>
      }
    }
    if (startOfWord != -1) builder += s.substring(startOfWord)

    builder.result()
  }
}

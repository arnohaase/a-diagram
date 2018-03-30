package com.ajjpj.adiagram.render.text

import com.ajjpj.adiagram.render.{TextAtomStyle, TextParagraphStyle, TextStyle}


case class TextModel(paragraphs: Vector[TextParagraphModel], style: TextStyle)
case class TextParagraphModel(atoms: Vector[TextAtomModel], style: TextParagraphStyle)
case class TextAtomModel(text: String, style: TextAtomStyle)
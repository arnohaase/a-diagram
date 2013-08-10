package com.ajjpj.adiagram.model

import com.ajjpj.adiagram.model.style._
import com.ajjpj.adiagram.model.diagram._
import scala.xml.{Node, Elem}
import javafx.scene.paint.Color
import java.util.UUID


/**
 * @author arno
 */
private[model] class DiagramDeserializer(root: Elem) {
  val styleRepository = new AStyleRepository
  val selectedStyles = new SelectedStyles
  val diagram = new ADiagram

  private var linkActions = List[() => Unit]()

  private val styleRepoRoot = root \ "style-repo"
  styleRepository.colors ++= styleRepoRoot \ "color" map colorFromXml
  styleRepository.fillStyles ++= styleRepoRoot \ "fill-solid" map solidFillFromXml
  styleRepository.fillStyles ++= styleRepoRoot \ "fill-linear" map linearFillFromXml
  styleRepository.lineStyles ++= styleRepoRoot \ "line-style" map lineStyleFromXml
  styleRepository.textStyles ++= styleRepoRoot \ "simple-text-style" map simpleTextStyleFromXml

  root \ "selected-styles" foreach selectedStylesFromXml

  root \ "diagram" foreach diagramFromXml

  linkActions.foreach { _() }

  //-----------------------------------------------------

  private def attrib(n: Node, name: String):  String = n.attribute(name).get.toString()
  private def attribD(n: Node, name: String): Double = attrib(n, name).toDouble

  //-----------------------------------------------------

  private def colorFromXml(e: Node) = {
    val colorSpec = new ColorSpec

    colorSpec.uuid = UUID.fromString(attrib(e, "id"))
    colorSpec.name = attrib(e, "name")
    colorSpec.color = Color.color(attribD(e, "red"), attribD(e, "green"), attribD(e, "blue"), attribD(e, "alpha"))

    colorSpec
  }

  private def solidFillFromXml(e: Node) = {
    val fill = new SolidFillSpec

    fill.uuid = UUID.fromString(attrib(e, "id"))
//    fill.name = attrib(e, "name")
    fill.colorSpec = styleRepository.colors.find(_.uuid.toString == attrib(e, "color")).get

    fill
  }

  private def linearFillFromXml(e: Node) = {
    val fill = new SimpleLinearGradientSpec

    fill.uuid = UUID.fromString(attrib(e, "id"))
//    fill.name = attrib(e, "name")
    fill.colorSpec0 = styleRepository.colors.find(_.uuid.toString == attrib(e, "color0")).get
    fill.colorSpec1 = styleRepository.colors.find(_.uuid.toString == attrib(e, "color1")).get

    fill
  }

  private def lineStyleFromXml(e: Node) = {
    val lineStyle = new LineStyleSpec

    lineStyle.uuid = UUID.fromString(attrib(e, "id"))
    lineStyle.width = attribD(e, "width")
    lineStyle.colorSpec = styleRepository.colors.find(_.uuid.toString == attrib(e, "color")).get

    lineStyle
  }

  private def simpleTextStyleFromXml(e: Node) = {
    val textStyle = new SimpleTextStyleSpec

    textStyle.uuid = UUID.fromString(attrib(e, "id"))
    textStyle.name = attrib(e, "name")
    textStyle.fontSizePixels = attribD(e, "font-size")

    textStyle
  }

  //-----------------------------------------------------

  private def selectedStylesFromXml(e: Node) = {
    selectedStyles.fillStyle     = styleRepository.fillStyles.  find(_.uuid.toString == attrib(e, "fill")).    get
    selectedStyles.shadowStyle   = styleRepository.shadowStyles.find(_.uuid.toString == attrib(e, "shadow")).  get
    selectedStyles.textStyle     = styleRepository.textStyles.  find(_.uuid.toString == attrib(e, "text")).    get
    selectedStyles.lineTextStyle = styleRepository.textStyles.  find(_.uuid.toString == attrib(e, "lineText")).get
    selectedStyles.lineStyle     = styleRepository.lineStyles.  find(_.uuid.toString == attrib(e, "line")).    get
    selectedStyles.startLineEnd  = styleRepository.lineEnds.    find(_.uuid.toString == attrib(e, "startEnd")).get
    selectedStyles.endLineEnd    = styleRepository.lineEnds.    find(_.uuid.toString == attrib(e, "endEnd")).  get
  }

  //TODO parameterized unapply factory for attributes

  //---------------------------------------------------------------

  private def diagramFromXml(e: Node) = diagram ++= e.child flatMap diagramElementFromXml

  private def diagramElementFromXml(e: Node): Option[AShapeSpec] = e match {
    case <box /> => Some(boxFromXml(e))
    case <text /> => Some(textFromXml(e))
    case <line /> => Some(lineFromXml(e))
    case _ => None
  }

  private def boxFromXml(e: Node) = {
    val id = UUID.fromString(attrib(e, "id"))
    val x = attribD(e, "x")
    val y = attribD(e, "y")
    val width = attribD(e, "width")
    val height = attribD(e, "height")
    val text = e.attribute("text").map(_.toString())
    val fillStyle   = styleRepository.fillStyles.  find(_.uuid.toString == attrib(e, "fill")).     get
    val shadowStyle = styleRepository.shadowStyles.find(_.uuid.toString == attrib(e, "shadow")).   get
    val textStyle   = styleRepository.textStyles.  find(_.uuid.toString == attrib(e, "textStyle")).get

    val box = new ABoxSpec((x, y), (width, height), text, fillStyle, shadowStyle, textStyle)
    box.uuid = id
    box
  }

  private def textFromXml(e: Node) = {
    val id = UUID.fromString(attrib(e, "id"))
    val x = attribD(e, "x")
    val y = attribD(e, "y")
    val width = attribD(e, "width")
    val height = attribD(e, "height")
    val text = attrib(e, "text")
    val style = styleRepository.textStyles.  find(_.uuid.toString == attrib(e, "style")).get

    val textSpec = new ATextSpec((x, y), (width, height), text, style)
    textSpec.uuid = id
    textSpec
  }

  private def lineFromXml(e: Node) = {
    val id = UUID.fromString(attrib(e, "id"))

    val x0 = e.attribute("x0").map(_.toString().toDouble)
    val y0 = e.attribute("y0").map(_.toString().toDouble)
    val x1 = e.attribute("x1").map(_.toString().toDouble)
    val y1 = e.attribute("y1").map(_.toString().toDouble)

    val box0Id = e.attribute("box0").map(x => UUID.fromString(x.toString()))
    val box1Id = e.attribute("box1").map(x => UUID.fromString(x.toString()))

    val text = e.attribute("text").map(_.toString())
    val style     =  styleRepository.lineStyles.find(_.uuid.toString == attrib(e, "style")).get
    val textStyle =  styleRepository.textStyles.find(_.uuid.toString == attrib(e, "textStyle")).get

    val startEnd = styleRepository.lineEnds.find(_.uuid.toString == attrib(e, "start")).get
    val endEnd   = styleRepository.lineEnds.find(_.uuid.toString == attrib(e, "end")).get

    val lineSpec = new ALineSpec(text, style, textStyle, startEnd, endEnd)
    if(x0.isDefined) {
      lineSpec.p0Source = LiteralPosSource(x0.get, y0.get)
    }
    else {
      linkActions = (() => {lineSpec.bindStartPoint(diagram.elements.find(_.uuid == box0Id.get).map(_.asInstanceOf[ABoxSpec]).get)}) :: linkActions
    }
    if(x1.isDefined) {
      lineSpec.p1Source = LiteralPosSource(x1.get, y1.get)
    }
    else {
      linkActions = (() => {lineSpec.bindEndPoint(diagram.elements.find(_.uuid == box1Id.get).map(_.asInstanceOf[ABoxSpec]).get)}) :: linkActions
    }

    lineSpec.uuid = id
    lineSpec
  }
}

package com.ajjpj.adiagram

import javafx.stage.Stage
import javafx.scene.paint._
import com.ajjpj.adiagram.model.{ATextSpec, ALineSpec, ADiagram, ABoxSpec}
import com.ajjpj.adiagram.render.base.{LineStyle, TextStyle, ShadowStyle, FillStyle}
import javafx.scene.effect.BlurType
import javafx.scene.text.TextAlignment
import javafx.geometry.VPos
import com.ajjpj.adiagram.ui.init.Init
import com.ajjpj.adiagram.geometry.{ADim, APoint}


/**
 * @author arno
 */
object ADiagramMain extends App {
  javafx.application.Application.launch(classOf[ADiagramMain])
}

class ADiagramMain extends javafx.application.Application {

  val fillStyle = new FillStyle(new LinearGradient(0.3, 0, .7, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.LIGHTBLUE), new Stop(1, Color.AZURE)))
  val shadowStyle = new ShadowStyle(6, 6, 16, BlurType.GAUSSIAN, Color.color(.5, .5, .5))
  val textStyle = new TextStyle(72, TextAlignment.CENTER, VPos.CENTER)
  val lineStyle = new LineStyle(Color.BLACK, 5)
  val lineTextStyle = new TextStyle(30, TextAlignment.CENTER, VPos.CENTER)

  val diagram = new ADiagram()
  val box1 = createBoxSpec((100.0, 200.0), (250.0, 80.0), Some("Hi Ho!"))
  val box2 = createBoxSpec((400.0, 400.0), (250.0, 80.0), Some("Yeah!"))
  diagram += box1
  diagram += box2
  diagram += new ALineSpec((1400.0, 100.0), (900.0, 500.0), Some("Arrow Text"), lineStyle, lineTextStyle)
  diagram += new ATextSpec((100.0, 600.0), (300.0, 80.0), "Hey Dude", textStyle)

  val connectingLine = new ALineSpec((0.0, 0.0), (0.0, 0.0), Some("Connecting"), lineStyle, lineTextStyle)
  connectingLine.bindStartPoint(box1)
  connectingLine.bindEndPoint  (box2)

  diagram += connectingLine

  private def createBoxSpec(pos: APoint, dim: ADim, text: Option[String]) = {
    val result = new ABoxSpec(dim, text, fillStyle, shadowStyle, textStyle)
    result.pos = pos
    result
  }

  override def start(stage: Stage) {
    Init.initStage(stage, diagram)
    stage.show()
  }

}


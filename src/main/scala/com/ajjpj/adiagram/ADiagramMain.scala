package com.ajjpj.adiagram

import javafx.stage.Stage
import com.ajjpj.adiagram.model.DiagramIO


/**
 * @author arno
 */
object ADiagramMain extends App {
  javafx.application.Application.launch(classOf[ADiagramMain])
}

class ADiagramMain extends javafx.application.Application {
  override def start(stage: Stage) {
    DiagramIO.init(stage)
  }
}


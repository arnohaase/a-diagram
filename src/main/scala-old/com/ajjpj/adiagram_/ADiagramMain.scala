package com.ajjpj.adiagram_

import javafx.stage.Stage
import com.ajjpj.adiagram_.model.DiagramIO


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


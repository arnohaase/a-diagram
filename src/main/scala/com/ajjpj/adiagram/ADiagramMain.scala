package com.ajjpj.adiagram

import javafx.stage.Stage
import com.ajjpj.adiagram.ui.init.Init
import com.ajjpj.adiagram.model.diagram.ADiagram
import com.ajjpj.adiagram.model.style.AStyleRepository
import com.ajjpj.adiagram.model.SelectedStyles


/**
 * @author arno
 */
object ADiagramMain extends App {
  javafx.application.Application.launch(classOf[ADiagramMain])
}

class ADiagramMain extends javafx.application.Application {
  override def start(stage: Stage) {
    val repo = AStyleRepository.default
    Init.initStage(stage, new ADiagram, repo, SelectedStyles.createFromDefaultRepo(repo))
    stage.show()
  }
}


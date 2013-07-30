package com.ajjpj.adiagram.ui.init

import javafx.stage.Stage
import com.ajjpj.adiagram.ui.fw.{MouseTracker, SelectionTracker, DiagramRootContainer, Digest}
import com.ajjpj.adiagram.model.ADiagram
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.control.ScrollPane


/**
 * @author arno
 */
object Init {
  def initStage(stage: Stage, diagram: ADiagram) {
    implicit val digest = new Digest()


    //TODO loosen the references using listeners?
    val root = new DiagramRootContainer()
    diagram.initRootContainer(root)

    val selections = new SelectionTracker(diagram, root)
    new MouseTracker(root, diagram, selections)

    val appPane = new BorderPane
    appPane.setTop(ADiagramMenuBar.create())

    val scrollPane = new ScrollPane()


    scrollPane.setContent(root)
    appPane.setCenter(scrollPane)

    val scene = new Scene(appPane, 1500, 1000)

    stage.setTitle("..... ") //TODO bind to the diagram name
    stage.setScene(scene)
  }
}

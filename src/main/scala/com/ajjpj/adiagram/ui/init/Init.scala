package com.ajjpj.adiagram.ui.init

import javafx.stage.Stage
import com.ajjpj.adiagram.ui.fw._
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.control.{Button, TitledPane, ScrollPane}
import com.ajjpj.adiagram.ui.{CurrentSelectionPane, SelectionTracker}
import scala.collection.JavaConversions
import com.ajjpj.adiagram.ui.presentation.{ADiagramController, DiagramRootContainer}
import com.ajjpj.adiagram.model.diagram.ADiagram


/**
 * @author arno
 */
object Init {
  def initStage(stage: Stage, diagram: ADiagram) {
    implicit val digest = new Digest()

    //TODO loosen the references using listeners?
    val root = new DiagramRootContainer()
    val controller = new ADiagramController(root, diagram)

    val appPane = new BorderPane
    appPane.setTop(ADiagramMenuBar.create(controller))
    appPane.setLeft(createAccordion(controller.selections))

    val scrollPane = new ScrollPane()
    scrollPane.setContent(root)
    appPane.setCenter(scrollPane)

    val scene = new Scene(appPane, 1500, 1000)

    stage.setTitle("..... ") //TODO bind to the diagram name
    stage.setScene(scene)

    digest.execute{} // trigger the digest loop
  }

  private def createAccordion(selections: SelectionTracker)(implicit digest: Digest) = {
    import JavaConversions._

    val accordion = JavaFxHelper.createUncollapsableAccordion()

    accordion.getPanes.add(new TitledPane("Selection", new CurrentSelectionPane(selections)))

    accordion.getPanes.add(new TitledPane("asdf", new Button("asdf")))
    accordion.getPanes.add(new TitledPane("jklö", new Button("jklö")))
    accordion.getPanes.add(new TitledPane("123", new Button("123")))

    val width = SystemConfiguration.leftAccordionWidth //TODO better approach? Must not change when content changes, though...
    accordion.getPanes.foreach (p => {
      p.setMinWidth(width)
      p.setMaxWidth(width)
    })

    accordion.setExpandedPane(accordion.getPanes.get(0))

    accordion
  }
}

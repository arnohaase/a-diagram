package com.ajjpj.adiagram.model

import javafx.stage.Stage
import com.ajjpj.adiagram.ui.fw._
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.control.{Button, TitledPane, ScrollPane}
import com.ajjpj.adiagram.ui.{CurrentSelectionPane, SelectionTracker}
import scala.collection.JavaConversions
import com.ajjpj.adiagram.ui.presentation.{ADiagramController, DiagramRootContainer}
import com.ajjpj.adiagram.model.diagram.ADiagram
import com.ajjpj.adiagram.model.style.AStyleRepository
import java.io.File
import com.ajjpj.adiagram.ui.init.ADiagramMenuBar


/**
 * @author arno
 */
private[model] object Init {
  def initEmptyStage(stage: Stage): ADiagramController = {
    val repo = AStyleRepository.default
    initStage(stage, new ADiagram, repo, SelectedStyles.createFromDefaultRepo(repo), None)
  }

  def initStage(stage: Stage, diagram: ADiagram, styleRepository: AStyleRepository, selectedStyles: SelectedStyles, file: File) : ADiagramController = {
    initStage(stage, diagram, styleRepository, selectedStyles, Some(file))
  }

  private def initStage(stage: Stage, diagram: ADiagram, styleRepository: AStyleRepository, selectedStyles: SelectedStyles, file: Option[File]) : ADiagramController = {
    implicit val digest = new Digest()

    //TODO loosen the references using listeners?
    val root = new DiagramRootContainer()
    val controller = new ADiagramController(root, diagram, styleRepository, selectedStyles, file)

    val appPane = new BorderPane
    appPane.setTop(ADiagramMenuBar.create(controller))
    appPane.setLeft(createAccordion(controller, controller.selections))

    val scrollPane = new ScrollPane()
    scrollPane.setContent(root)
    appPane.setCenter(scrollPane)

    val scene = new Scene(appPane, 1500, 1000) //TODO initial window size and pos

    digest.bind(stage.titleProperty, controller.windowTitle)
    stage.setScene(scene)

    digest.execute{} // trigger the digest loop

    controller
  }

  private def createAccordion(ctrl: ADiagramController, selections: SelectionTracker)(implicit digest: Digest) = {
    import JavaConversions._

    val accordion = JavaFxHelper.createUncollapsableAccordion()

    accordion.getPanes.add(new TitledPane("Selection", new CurrentSelectionPane(ctrl, selections)))

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

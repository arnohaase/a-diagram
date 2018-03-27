package com.ajjpj.adiagram_.model

import javafx.stage.Stage
import com.ajjpj.adiagram_.ui.fw._
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.control.{Button, TitledPane, ScrollPane}
import com.ajjpj.adiagram_.ui.{ADiagramController, ADiagramMenuBar, SelectionTracker}
import scala.collection.JavaConversions
import com.ajjpj.adiagram_.ui.presentation.{DiagramRootContainer}
import com.ajjpj.adiagram_.model.diagram.ADiagram
import com.ajjpj.adiagram_.model.style.AStyleRepository
import java.io.File
import com.ajjpj.adiagram_.ui.accordion._
import scala.Some
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.application.Platform


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
    appPane.setLeft(createAccordion(controller))

    val scrollPane = new ScrollPane()
    scrollPane.setContent(root)
    appPane.setCenter(scrollPane)

    digest.registerEventSource(scrollPane.viewportBoundsProperty)
    Platform.runLater(new Runnable {
      override def run {
        digest.bindDouble(root.minWidthProperty,  scrollPane.getViewportBounds.getWidth)
        digest.bindDouble(root.minHeightProperty, scrollPane.getViewportBounds.getHeight)
      }
    })

    val scene = new Scene(appPane, 1500, 1000) //TODO initial window size and pos

    digest.bind(stage.titleProperty, controller.windowTitle)
    stage.setScene(scene)

    digest.execute{} // trigger the digest loop

    controller
  }

  private def createAccordion(ctrl: ADiagramController)(implicit digest: Digest) = {
    import JavaConversions._

    val accordion = JavaFxHelper.createUncollapsableAccordion()

    accordion.getPanes.add(new TitledPane("Selection", new CurrentSelectionPane(ctrl)))
    accordion.getPanes.add(new TitledPane("Current Style", new CurrentStylesPane(ctrl)))
    accordion.getPanes.add(new TitledPane("Colors", new ColorPane(ctrl)))
    accordion.getPanes.add(new TitledPane("Fill Styles", new FillStylePane(ctrl)))
    accordion.getPanes.add(new TitledPane("Text Styles", new TextStylePane(ctrl)))
    accordion.getPanes.add(new TitledPane("Line Styles", new LineStylePane(ctrl)))

    val width = SystemConfiguration.leftAccordionWidth //TODO better approach? Must not change when content changes, though...
    accordion.getPanes.foreach (p => {
      p.setMinWidth(width)
      p.setMaxWidth(width)
    })

    accordion.setExpandedPane(accordion.getPanes.get(0))

    accordion
  }
}

package com.ajjpj.adiagram.snippets

import javafx.stage.Stage
import javafx.beans.property.adapter.{ReadOnlyJavaBeanIntegerPropertyBuilder, ReadOnlyJavaBeanIntegerProperty}
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.layout.BorderPane
import javafx.scene.{Group, Scene}
import javafx.scene.control.{TitledPane, Button, Accordion}
import com.ajjpj.adiagram.ui.fw._
import javafx.scene.text.Text
import scala.beans.BeanProperty
import scala.Some

/**
 * @author arno
 */
object JavaFxMain extends App {
  javafx.application.Application.launch(classOf[JavaFxMain])
}

class JavaFxMain extends javafx.application.Application {
  import JavaFxHelper._

  implicit val digest = new Digest()

  var c=0
  var stage: Stage = _

  class Counter {
    @BeanProperty var num=0
    def inc() = num+=1
  }

  val counter = new Counter()
  var rawCounterProp: ReadOnlyJavaBeanIntegerProperty = _
  val counterProperty = new SimpleIntegerProperty()

  override def start(stage: Stage) {
    rawCounterProp = new ReadOnlyJavaBeanIntegerPropertyBuilder().bean(counter).name("num").build()
    counterProperty.bind(rawCounterProp)

    this.stage = stage
    stage.setTitle("Dummy JafvaFX Application")

    val root = new BorderPane()
    val scene = new Scene(root, 1500, 900)

    root.setTop(menuBar)
    root.setLeft(accordion)

    val g = new Group()
    g.getChildren.addAll(new Text("xyz"))
    root.setCenter(g)

    stage.setScene(scene)
    stage.show()
  }

  val menuBar = {
    val openAction = new SimpleAction(text="Open", accelerator = Some("Ctrl+O"), body= {
      counter.inc()
      println(showOkCancelDialog(stage, "My Dialog", new Text(25, 25, "Hi There!")))
    })
    val saveAction = new SimpleAction(text="Save", body={})
    val showAccordionAction = new SimpleAction("Show Accordion", accelerator = Some("Ctrl+L"), body={accordion.setVisible(! accordion.isVisible)})
    val fullScreenAction = new SimpleAction("Full Screen", accelerator = Some("F11"), body={stage.setFullScreen(! stage.isFullScreen)})

    val dummyAction = new SimpleAction(text="dummy", visible=c%5 != 4, enabled = c%2 != 0, body={})
    val dummyAction2 = new SimpleAction(text="dummy2", body={c+=1})

    val fileMenu = new SimpleActionGroup(text="File", items=List(openAction, saveAction))
    val viewMenu = new SimpleActionGroup(text="View", items=List(showAccordionAction, fullScreenAction))
    val dummyMenu = new SimpleActionGroup(text="Dummy", items=List(dummyAction, dummyAction2))

    Action.createMenuBar(fileMenu, viewMenu, dummyMenu)
  }

  val accordion = {
    val result=new Accordion()
    result.getPanes().add(new TitledPane("asdf", new Button("asdf")))
    result.getPanes().add(new TitledPane("jklö", new Button("jklö")))
    result.getPanes().add(new TitledPane("123", new Button("123")))

    result.setExpandedPane(result.getPanes.get(0))
    result
  }
}



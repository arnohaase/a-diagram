package com.ajjpj.adiagram.model

import com.ajjpj.adiagram.ui.presentation.ADiagramController
import java.io.File
import javafx.stage.{Stage, FileChooser}
import scala.xml.XML
import com.ajjpj.adiagram.ui.init.Init

/**
 * @author arno
 */
object DiagramIO {
  def open(ctrl: ADiagramController) {
    val fileChooser = new FileChooser
    fileChooser.setTitle("Open Diagram")
    //TODO set initial directory
    //TODO extension filter
    val file = fileChooser.showOpenDialog(ctrl.root.getScene.getWindow)
    if(file != null) {
      doOpen(file)
    }
  }

  def save(ctrl: ADiagramController) {
    val fileChooser = new FileChooser
    fileChooser.setTitle("Save Diagram")
    //TODO set initial directory
    //TODO set previous file name (?)
    //TODO extension filter
    val file = fileChooser.showSaveDialog(ctrl.root.getScene.getWindow)
    if(file != null) {
      doSave(file, ctrl)
    }
  }

  def saveAs(ctrl: ADiagramController) {
    val fileChooser = new FileChooser
    fileChooser.setTitle("Save Diagram")
    //TODO set initial directory
    //TODO set previous file name (?)
    //TODO extension filter
    val file = fileChooser.showSaveDialog(ctrl.root.getScene.getWindow)
    if(file != null) {
      doSave(file, ctrl)
    }
  }


  private[model] def doOpen(file: File) {
    println ("opening " + file.getAbsolutePath)

    val deser = new DiagramDeserializer(XML.loadFile(file))

    val stage = new Stage()
    Init.initStage(stage, deser.diagram, deser.styleRepository, deser.selectedStyles)
    stage.show()
  }

  private[model] def doSave(file: File, ctrl: ADiagramController) {
    println ("saved to " + file.getAbsolutePath)
    println(new DiagramSerializer(ctrl).toXml)

    if(file.exists) {
      println("file exists - skipping")
    }
    else {
      XML.save(file.getPath,new DiagramSerializer(ctrl).toXml, xmlDecl=true, enc="UTF-8")
    }
  }
}

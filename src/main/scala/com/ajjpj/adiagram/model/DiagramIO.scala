package com.ajjpj.adiagram.model

import com.ajjpj.adiagram.ui.presentation.ADiagramController
import java.io.File
import javafx.stage.{Stage, FileChooser}
import scala.xml.XML
import com.ajjpj.adiagram.ui.init.Init
import com.ajjpj.adiagram.ui.fw.Digest

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
      doOpen(file, ctrl)
    }
  }

  def save(ctrl: ADiagramController)(implicit digest: Digest) {
    ctrl.file match {
      case Some(file) => doSave(file, ctrl)
      case None => saveAs(ctrl)
    }
  }

  def saveAs(ctrl: ADiagramController)(implicit digest: Digest) {
    val fileChooser = new FileChooser
    fileChooser.setTitle("Save Diagram")
    //TODO set initial directory
    //TODO set previous file name (?)
    //TODO extension filter
    val file = fileChooser.showSaveDialog(ctrl.root.getScene.getWindow)
    if(file != null) {
      if(file.exists) {
        println("file exists - skipping") //TODO pop up 'overwrite?' confirm dialog
      }
      else {
        doSave(file, ctrl)
      }
    }
  }

  private[model] def doOpen(file: File, ctrl: ADiagramController) {
    val deser = new DiagramDeserializer(XML.loadFile(file))

    if(ctrl.isPristine) {
      val stage = ctrl.root.getScene.getWindow.asInstanceOf[Stage]
      Init.initStage(stage, deser.diagram, deser.styleRepository, deser.selectedStyles, file)
    }
    else {
      val stage = new Stage()
      Init.initStage(stage, deser.diagram, deser.styleRepository, deser.selectedStyles, file)
      stage.show()
    }
  }

  private[model] def doSave(file: File, ctrl: ADiagramController)(implicit digest: Digest) {
    XML.save(file.getPath,new DiagramSerializer(ctrl).toXml, xmlDecl=true, enc="UTF-8")
    ctrl.file = Some(file)
    digest.undoRedo.clear()
  }
}

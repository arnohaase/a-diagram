package com.ajjpj.adiagram.model

import com.ajjpj.adiagram.ui.presentation.ADiagramController
import java.io.File
import javafx.stage.{Stage, FileChooser}
import scala.xml.XML
import com.ajjpj.adiagram.ui.init.Init
import com.ajjpj.adiagram.ui.fw.{JavaFxHelper, Digest}
import javafx.stage.FileChooser.ExtensionFilter
import javafx.scene.control.Label

/**
 * @author arno
 */
object DiagramIO {
  def open(ctrl: ADiagramController) {
    val fileChooser = new FileChooser
    fileChooser.setTitle("Open Diagram")
    fileChooser.getExtensionFilters.add(extensionFilter)
    //TODO set initial directory
    val file = fileChooser.showOpenDialog(ctrl.window)
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

  private val extensionFilter = new ExtensionFilter("Diagram Files", "*.adiagram")

  def saveAs(ctrl: ADiagramController)(implicit digest: Digest) {
    val fileChooser = new FileChooser
    fileChooser.setTitle("Save Diagram")
    fileChooser.getExtensionFilters.add(extensionFilter)
    //TODO set initial directory
    //TODO set previous file name (?)
    val fileRaw = fileChooser.showSaveDialog(ctrl.window)
    if(fileRaw != null) {
      val file = if(fileRaw.getName endsWith ".adiagram") fileRaw else new File(fileRaw.getParentFile, fileRaw.getName + ".adiagram")

      if(file.exists) {
        val overwrite = JavaFxHelper.showOkCancelDialog(ctrl.window, "Confirm Overwrite", new Label("This file exists. Do you want to overwrite it?"))
        if(overwrite) {
          doSave(file, ctrl)
        }
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

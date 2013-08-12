package com.ajjpj.adiagram.model

import com.ajjpj.adiagram.ui.presentation.ADiagramController
import java.io.File
import javafx.stage.{Stage, FileChooser}
import scala.xml.XML
import com.ajjpj.adiagram.ui.fw.{JavaFxHelper, Digest}
import javafx.stage.FileChooser.ExtensionFilter
import javafx.scene.control.Label


/**
 * @author arno
 */
object DiagramIO {
  private var stages = Map[Stage, Option[ADiagramController]]()

  def init(stage: Stage) {
    val ctrl = Init.initEmptyStage(stage)
    stages += (stage -> Some(ctrl))
    stage.show()
  }

  def newDiagram() {
    val stage = new Stage
    val ctrl = Init.initEmptyStage(stage)
    stages += (stage -> Some(ctrl))
    stage.show()
  }

  def closeAll(): Boolean = {
    stages.values.foreach(_ match {
      case Some(ctrl) => ctrl.window.toFront(); if(! close(ctrl)(ctrl.digest)) return false
      case None =>
    })
    true
  }

  def exit() {
    if(closeAll()) {
      System.exit(0)
    }
  }


  /**
   * @return true iff the diagram was actually closed
   */
  def close(ctrl: ADiagramController)(implicit digest: Digest): Boolean = {
    import JavaFxHelper._

    def doClose(): Boolean = {
      val stage = ctrl.window
      if(stages.size == 1) {
        stage.setScene(null)
        val newCtrl = Init.initEmptyStage(stage)
        stages += (stage -> Some(newCtrl))
      }
      else {
        stage.close()
        stages -= stage
      }

      true
    }

    if(ctrl.isDirty) {
      val btnSave    = ButtonSpec(text="Save",    clickId="save", default=true)
      val btnDiscard = ButtonSpec(text="Discard", clickId="discard")
      val btnCancel  = ButtonSpec(text="Cancel",  clickId="cancel", cancel=true)

      showSingleClickDialog(ctrl.window, "Unsaved Diagram", new Label("Unsaved diagram. How do you want to proceed?"), btnSave, btnDiscard, btnCancel) match {
        case "save"    => save(ctrl); doClose() //TODO handle 'cancel' during save operation
        case "discard" => doClose()
        case "cancel"  => false
      }
    }
    else {
      doClose()
    }
  }

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

      if(JavaFxHelper.confirmOverwrite(file, ctrl.window)) doSave(file, ctrl)
    }
  }

  private[model] def doOpen(file: File, ctrl: ADiagramController) {
    val deser = new DiagramDeserializer(XML.loadFile(file))

    if(ctrl.isPristine) {
      val stage = ctrl.root.getScene.getWindow.asInstanceOf[Stage]
      val newCtrl = Init.initStage(stage, deser.diagram, deser.styleRepository, deser.selectedStyles, file)
      stages += (stage -> Some(newCtrl))
    }
    else {
      val stage = new Stage()
      val newCtrl = Init.initStage(stage, deser.diagram, deser.styleRepository, deser.selectedStyles, file)
      stages += (stage -> Some(newCtrl))
      stage.show()
    }
  }

  private[model] def doSave(file: File, ctrl: ADiagramController)(implicit digest: Digest) {
    XML.save(file.getPath,new DiagramSerializer(ctrl).toXml, xmlDecl=true, enc="UTF-8")
    ctrl.file = Some(file)
    digest.undoRedo.clear()
  }
}

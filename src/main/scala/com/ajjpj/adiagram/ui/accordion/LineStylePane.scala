package com.ajjpj.adiagram.ui.accordion

import com.ajjpj.adiagram.ui.{StyleTreeCellFactory, ADiagramController}
import com.ajjpj.adiagram.ui.fw.Digest
import javafx.scene.layout.Pane
import javafx.scene.control.{TreeView, TreeItem}


/**
  * @author arno
  */
class LineStylePane(ctrl: ADiagramController)(implicit digest: Digest) extends Pane {
 val root = new TreeItem[AnyRef]("root")
 val tree = new TreeView(root)
 getChildren.add(tree)

 tree.setShowRoot(false)
 tree.setCellFactory(StyleTreeCellFactory)

  //TODO preview for selected; show details

 //TODO bind
 ctrl.styleRepository.lineStyles.foreach(s => root.getChildren.add(new TreeItem(s)))
 }

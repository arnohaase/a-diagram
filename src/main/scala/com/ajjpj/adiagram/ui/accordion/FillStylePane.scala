package com.ajjpj.adiagram.ui.accordion

import com.ajjpj.adiagram.ui.{StyleTreeCellFactory, ADiagramController}
import com.ajjpj.adiagram.ui.fw.Digest
import javafx.scene.layout.Pane
import javafx.scene.control.{TreeView, TreeItem}


/**
 * @author arno
 */
class FillStylePane (ctrl: ADiagramController)(implicit digest: Digest) extends Pane {
  val root = new TreeItem[AnyRef]("root")
  val tree = new TreeView(root)
  getChildren.add(tree)

  tree.setShowRoot(false)
  tree.setCellFactory(StyleTreeCellFactory)

  //TODO bind
  ctrl.styleRepository.fillStyles.foreach(fs => root.getChildren.add(new TreeItem(fs)))
}

package com.ajjpj.adiagram.ui.accordion

import javafx.scene.layout.Pane
import com.ajjpj.adiagram.ui.fw.Digest
import com.ajjpj.adiagram.ui.{FillStyleTreeCell, ColorTreeCell, ADiagramController}
import javafx.scene.control.{TreeCell, TreeItem, TreeView}
import javafx.util.Callback
import com.ajjpj.adiagram.model.style.{SimpleLinearGradientSpec, SolidFillSpec}


/**
 * @author arno
 */
class ColorPane(ctrl: ADiagramController)(implicit digest: Digest) extends Pane {
  val root = new TreeItem[AnyRef]("root")
  val tree = new TreeView(root)
  getChildren.add(tree)


  tree.setShowRoot(false)
  tree.setCellFactory(new Callback[TreeView[AnyRef], TreeCell[AnyRef]] {
    def call(p1: TreeView[AnyRef]): TreeCell[AnyRef] = {
      val colorCell = new ColorTreeCell().asInstanceOf[TreeCell[AnyRef]]
      val fillStyleCell = new FillStyleTreeCell().asInstanceOf[TreeCell[AnyRef]]

      colorCell
    }
  })

  //TODO bind
  ctrl.styleRepository.colors.foreach(c => {
    val colorItem = new TreeItem[AnyRef](c)
    val usingFillStyles = ctrl.styleRepository.
      fillStyles.
      filter(_ match {
      case fs: SolidFillSpec => fs.colorSpec == c
      case fs: SimpleLinearGradientSpec => fs.colorSpec0 == c || fs.colorSpec1 == c
      })

//    usingFillStyles.foreach(fs => colorItem.getChildren.add(new TreeItem(fs)))

    root.getChildren.add(colorItem)
  })


}

package com.ajjpj.adiagram.ui.accordion

import javafx.scene.layout.Pane
import com.ajjpj.adiagram.ui.fw.Digest
import com.ajjpj.adiagram.ui.{StyleTreeCellFactory, FillStyleTreeCell, ColorTreeCell, ADiagramController}
import javafx.scene.control.{TreeCell, TreeItem, TreeView}
import javafx.util.Callback
import com.ajjpj.adiagram.model.style.{FillStyleSpec, ColorSpec, SimpleLinearGradientSpec, SolidFillSpec}


/**
 * @author arno
 */
class ColorPane(ctrl: ADiagramController)(implicit digest: Digest) extends Pane {
  val root = new TreeItem[AnyRef]("root")
  val tree = new TreeView(root)
  getChildren.add(tree)

  tree.setShowRoot(false)
  tree.setCellFactory(StyleTreeCellFactory)

  digest.watch(ctrl.styleRepository.changeCounter, refresh _)
  refresh()

  def refresh() {
    root.getChildren.clear()

    ctrl.styleRepository.colors.foreach(c => {
      val colorItem = new TreeItem[AnyRef](c)
      val usingFillStyles = ctrl.styleRepository.
        fillStyles.
        filter(_ match {
        case fs: SolidFillSpec => fs.colorSpec == c
        case fs: SimpleLinearGradientSpec => fs.colorSpec0 == c || fs.colorSpec1 == c
      })

      usingFillStyles.foreach(fs => colorItem.getChildren.add(new TreeItem(fs)))

      ctrl.styleRepository.lineStyles.filter(_.colorSpec == c).foreach(ls => colorItem.getChildren.add(new TreeItem(ls)))

      root.getChildren.add(colorItem)
    })
  }
}

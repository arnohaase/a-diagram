package com.ajjpj.adiagram.ui

import com.ajjpj.adiagram.model.ADiagram
import com.ajjpj.adiagram.ui.fw.{Digest, DiagramRootContainer}


/**
 * @author arno
 */
class ADiagramController (root: DiagramRootContainer, diagram: ADiagram)(implicit digest: Digest) {
  val selections = new SelectionTracker(diagram, root)
  val mouseTracker = new MouseTracker(root, diagram, selections)

}

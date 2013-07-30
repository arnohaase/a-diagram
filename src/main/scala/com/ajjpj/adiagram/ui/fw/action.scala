package com.ajjpj.adiagram.ui.fw

import javafx.scene.Node
import javafx.scene.input.KeyCombination
import javafx.scene.control._


/**
 * This is a marker interface for either actions or groups of actions. There are two kinds of ActionMenuPart:
 * Action and ActionGroup (plus 'SEPARATOR' as a special case). <p />
 *
 * They have a lot in common, but only Actions can be executed whereas ActionGroups contain a list of actions.
 *
 *
 * @author arno
 */
trait ActionMenuPart {
  def propEnabled: Boolean
  def propVisible: Boolean
  def propText: String
  def propLongText: Option[String]
  def propGraphic: Option[Node]
}

trait Action extends ActionMenuPart {
  def propAccelerator: Option[KeyCombination]
  def apply(): Unit
}

trait ActionGroup extends ActionMenuPart {
  def propItems: Iterable[ActionMenuPart]
}

class SimpleAction(text: => String, longText: => Option[String] = None,
                   accelerator: => Option[KeyCombination] = None, graphic: => Option[Node] = None,
                   enabled: => Boolean = true, visible: => Boolean = true,
                   body: => Unit)(implicit digest: Digest) extends Action {
  override def propText = text
  override def propLongText = longText
  override def propAccelerator = accelerator
  override def propGraphic = graphic
  override def propEnabled = enabled
  override def propVisible = visible

  override def apply() = digest.execute(body)
}

class SimpleActionGroup(text: => String, longText: => Option[String] = None,
                         enabled: => Boolean = true, visible: => Boolean = true,
                         graphic: => Option[Node] = None,
                         items: => Iterable[ActionMenuPart]) extends ActionGroup {
  override def propText = text
  override def propLongText = longText
  override def propEnabled = enabled
  override def propVisible = visible
  override def propGraphic = graphic
  override def propItems = items
}


object Action {
  val SEPARATOR = new ActionMenuPart {
    override def propEnabled  = throw new UnsupportedOperationException
    override def propVisible  = throw new UnsupportedOperationException
    override def propText     = throw new UnsupportedOperationException
    override def propLongText = throw new UnsupportedOperationException
    override def propGraphic  = throw new UnsupportedOperationException
  }

  private def createMenuPart(part: ActionMenuPart)(implicit digest: Digest): MenuItem = part match {
    case action: Action => createMenuItem(action)
    case SEPARATOR => new SeparatorMenuItem()
    case actionGroup: ActionGroup =>
      val menu = configure(new Menu(), actionGroup)
      actionGroup.propItems.foreach(part => menu.getItems().add(createMenuPart(part)))
      menu
  }

  def createMenuItem(action: Action)      (implicit digest: Digest)          = configure(new MenuItem(), action)
  def createMenu(actionGroup: ActionGroup)(implicit digest: Digest): Menu    = createMenuPart(actionGroup).asInstanceOf[Menu]
  def createMenuBar(items: ActionGroup*)  (implicit digest: Digest): MenuBar = createMenuBar(items.toList)
  def createMenuBar(items: Iterable[ActionGroup])(implicit digest: Digest): MenuBar = {
    val menuBar = new MenuBar()
    items.foreach(part => menuBar.getMenus().add(createMenu(part)))
    menuBar
  }

  //TODO tool bar
  //TODO button bar
  //TODO context menu


  //--------------------

  //TODO default button

  private def configure[T <: ButtonBase](btn: T, action: Action)(implicit digest: Digest) = {
    digest.bind(btn.textProperty(), action.propText)
    digest.bind(btn.disableProperty(), (! action.propEnabled).asInstanceOf[java.lang.Boolean])
    digest.bind(btn.visibleProperty(), action.propVisible.asInstanceOf[java.lang.Boolean])
    digest.bind(btn.graphicProperty(), action.propGraphic.getOrElse(null))

    // tooltip requires some special handling (i.e. don't have one when the propText property is null
    digest.bind(btn.tooltipProperty(), action.propLongText match {
      case None => null
      case Some(s) =>
        val tooltip = new Tooltip()
        tooltip.setText(s)
        tooltip
    })

    // TODO handle the selected state of the button if it is of the applicable type

    btn.setOnAction(digest.createEventHandler(_ => action()))

    btn
  }

  /**
   * special case: MenuItem is not a subtype of ButtonBase
   */
  private def configure[T <: MenuItem] (item: T, part: ActionMenuPart)(implicit digest: Digest) = {
    digest.bind(item.textProperty(), part.propText)
    digest.bind(item.disableProperty(), (! part.propEnabled).asInstanceOf[java.lang.Boolean])
    digest.bind(item.visibleProperty(), part.propVisible.asInstanceOf[java.lang.Boolean])
    digest.bind(item.graphicProperty(), part.propGraphic.getOrElse(null))

    // TODO handle the selected state of the menu item if it is a CheckMenuItem or RadioMenuItem

    part match {
      case action: Action =>
        digest.bind(item.acceleratorProperty(), action.propAccelerator.getOrElse(null))
        item.setOnAction(digest.createEventHandler(_ => action()))
      case _ =>
    }

    item
  }
}


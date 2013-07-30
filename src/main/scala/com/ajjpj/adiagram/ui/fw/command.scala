package com.ajjpj.adiagram.ui.fw



trait Command {
  def name: String
  def undo(): Unit
  def redo(): Unit
}


class UndoRedoStack {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def nextUndo = undoStack match {
    case head :: tail => Some(head)
    case _ => None
  }
  def nextRedo = redoStack match {
    case head :: tail => Some(head)
    case _ => None
  }

  def hasUndo = nextUndo.isDefined
  def hasRedo = nextRedo.isDefined

  def push(cmd: Command) {
    //TODO limit size
    undoStack = cmd :: undoStack
    redoStack = Nil
  }

  def undo() = undoStack match {
      case head :: tail =>
        head.undo()
        undoStack = tail
        redoStack = head :: redoStack
      case _ =>
    }

  def redo() = redoStack match {
    case head :: tail =>
      head.redo()
      redoStack = tail
      undoStack = head :: undoStack
    case _ =>
  }
}
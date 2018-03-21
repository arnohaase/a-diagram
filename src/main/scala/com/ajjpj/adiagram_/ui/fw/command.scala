package com.ajjpj.adiagram_.ui.fw



trait Command {
  def name: String
  def isNop: Boolean
  def undo(): Unit
  def redo(): Unit
}


class UndoRedoStack {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def clear() {
    undoStack = Nil
    redoStack = Nil
  }

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
    if (! cmd.isNop) {
      undoStack = cmd :: undoStack
      redoStack = Nil
    }
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
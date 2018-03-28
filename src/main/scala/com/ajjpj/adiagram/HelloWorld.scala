package com.ajjpj.adiagram

import javafx.application.Application
import javafx.event.ActionEvent
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.effect.{BlurType, DropShadow}
import javafx.scene.paint.Color
import javafx.stage.Stage

object HelloWorld extends App {
  Application.launch (classOf[HelloWorld])
}

class HelloWorld extends Application {
  def start (primaryStage: Stage) = {
    primaryStage.setTitle ("Hello World!")
    val btn = new Button()
    btn.setText ("Say 'Hello World'")
    btn.setOnAction ((event: ActionEvent) => {
      System.out.println ("Hello World!")
    })

    val btn2 = new Button("yo")

    btn.setEffect(new DropShadow(200, Color.BLACK))
    btn2.setEffect(new DropShadow(BlurType.ONE_PASS_BOX, Color.BLACK, 100, 1, 0, 0))

    import javafx.geometry.Pos
    import javafx.scene.layout.GridPane
    val grid = new GridPane
    grid.setAlignment (Pos.CENTER)
    grid.setHgap (2)
    grid.setVgap (2)
    grid.setPadding (new Insets (25, 25, 25, 25))

    grid.add(btn, 0, 0)
    grid.add(btn2, 1, 0)

    primaryStage.setScene (new Scene (grid, 300, 250))
    primaryStage.show ()
  }
}
package com.ajjpj.adiagram

import com.ajjpj.adiagram.geometry.{LenUnit, Length, Vector2}
import com.ajjpj.adiagram.render._
import com.ajjpj.adiagram.render.lineend.NullLineEnd
import javafx.application.Application
import javafx.geometry.VPos
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.{Button, Label}
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.scene.text._
import javafx.stage.Stage
import com.ajjpj.adiagram.render.text._
import javafx.scene.image.ImageView

object HelloWorld extends App {
  Application.launch (classOf[HelloWorld])
}

class HelloWorld extends Application {
  def start (primaryStage: Stage) = {
    val root = new FlowPane
    root.setRowValignment(VPos.TOP)

    {
      val atom = TextAtomModel("Hallo Arno asdfj asdf adj asdl adfslaka asdjlf alksdj ljasdfklasd jfklsjadklfjlkasdf lasdkasd lasdlfkl           lkasdfk lasdfkl asdkfl kasdf", TextAtomStyle("Arial", Length(20, LenUnit.pt), Color.DARKBLUE))
      val paragraph = TextParagraphModel(Vector(atom), TextParagraphStyle(TextAlignment.LEFT))
      val text = TextModel(Vector(paragraph), TextStyle())
      val riText = RenderableText(Vector2.ZERO, Length(200, LenUnit.pt), text, None).render(new Model2Screen(1))
      root.getChildren.add(new ImageView(riText.img))
    }
    {
      val atom = TextAtomModel("Hallo Arno asdfj asdf adj asdl adfslaka asdjlf alksdj ljasdfklasd jfklsjadklfjlkasdf lasdkasd lasdlfkl           lkasdfk lasdfkl asdkfl kasdf", TextAtomStyle("Arial", Length(20, LenUnit.pt), Color.DARKBLUE))
      val paragraph = TextParagraphModel(Vector(atom), TextParagraphStyle(TextAlignment.JUSTIFY))
      val text = TextModel(Vector(paragraph), TextStyle())
      val riText = RenderableText(Vector2.ZERO, Length(200, LenUnit.pt), text, None).render(new Model2Screen(1))
      root.getChildren.add(new ImageView(riText.img))
    }

    val riLine = RenderableStraightLine(Vector2.ZERO, Vector2(100, 100, LenUnit.pt), LineStyle(Length(1, LenUnit.pt), Color.DARKRED), new NullLineEnd(), new NullLineEnd()).render(new Model2Screen(1))
    root.getChildren.add(new ImageView(riLine.img))


//    val text = new Text("123 456 789")
//    text.setWrappingWidth(80)
//    text.setFont(Font.font ("Helvetica", FontPosture.ITALIC, 40))
//    root.getChildren.add(text)
//
//    val c = new Canvas(600, 600)
//    val gc = c.getGraphicsContext2D
//    gc.setFill(Color.BROWN)
//
//    val fontMetrics = new FontMetrics (Font.font ("Helvetica", FontPosture.ITALIC, 40))
//
//    println (fontMetrics)
//
//    gc.setStroke(Color.DARKBLUE)
//    gc.strokeLine(0, fontMetrics.ascent, 200, fontMetrics.ascent)
//
//    gc.setStroke(Color.DARKGREEN)
////    gc.strokeLine(0, 100 - fontMetrics.ascent, 200, 100 - fontMe/trics.ascent)
//    gc.strokeLine(0, fontMetrics.ascent + fontMetrics.descent, 200, fontMetrics.ascent + fontMetrics.descent)
//
//    gc.setStroke(Color.DARKRED)
//    gc.strokeLine(200, 0, 200, 3*fontMetrics.lineHeight)
//    gc.strokeLine(204, 0, 204, text.getLayoutBounds.getHeight)
//
//    gc.rotate(20)
//
//
//    gc.setFont(Font.font ("Helvetica", FontPosture.ITALIC, 40))
//    gc.fillText("40", 0, fontMetrics.ascent)
//
//    gc.setFont(Font.font ("Helvetica", FontPosture.ITALIC, 20))
//    gc.fillText("20", 50, fontMetrics.ascent)
//
//    gc.setFont(Font.font ("Helvetica", FontPosture.ITALIC, 10))
//    gc.fillText("10", 100, fontMetrics.ascent)
//
//    root.getChildren.add(c)


//    val tf = new TextFlow
//    sp.getChildren.add(tf)
//
//
//    import javafx.scene.text.{Font, FontPosture, FontWeight}
//
//    val text1 = new Text ("Big italic red")
//    text1.setFill (Color.RED)
//    text1.setFont (Font.font ("Helvetica", FontPosture.ITALIC, 40))
//    text1.setUnderline(true)
//
//    val text1a = new Text(" textBig italic red textBig italic red textBig italic red textBig italic red")
//    text1a.setFont (Font.font ("Helvetica", FontPosture.ITALIC, 40))
//
//    val text2 = new Text (" little bold blue text little bold blue text little bold blue text little bold blue text")
//    text2.setFill (Color.BLUE)
//    text2.setFont (Font.font ("Helvetica", FontWeight.BOLD, 10))
//    text2.setStrikethrough(true)
//
//    tf.getChildren.add(text1)
//    tf.getChildren.add(text1a)
//    tf.getChildren.add(text2)
//
//    tf.setMaxWidth(300)
//    tf.setTextAlignment(TextAlignment.JUSTIFY)
//    tf.setRotate(-10)
//    tf.setBorder(new Border(Array(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(2))), Array.empty[BorderImage]))

//    sp.layout()
//    tf.layout()
//
//    println ("local: " + tf.getBoundsInLocal)
//    println ("parent: " + tf.getBoundsInParent)
//
//    val ri = RenderedItem.fromNode(Vector2.ZERO, new Model2Screen(1), None, tf)
//    println (ri.img.getWidth + " x " + ri.img.getHeight)
//
//    ImageIO.write(SwingFXUtils.fromFXImage(ri.img, null), "png", new File("test.png"))


    primaryStage.setScene (new Scene (root, 1000, 750))
    primaryStage.show ()
  }
}
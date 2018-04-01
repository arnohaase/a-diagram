package render_tests

import java.io.File

import com.sun.javafx.application.PlatformImpl

trait RenderUtils {
  /**
    * used as the last path segment for the folder in which this spec's output is generated
    */
  def componentId: String

  PlatformImpl.startup(() => {})
  val baseDir = new File(s"target/render-test/$componentId")
  baseDir.mkdirs()
  delRec(baseDir)


  def delRec(f: File): Unit = {
    Option(f.listFiles).getOrElse(Array.empty).foreach(delRec)
    if (f.isFile) f.delete()
  }

}

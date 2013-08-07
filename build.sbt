import AssemblyKeys._ // put this at the top of the file

assemblySettings

javaHome := {
  var s = System.getenv("JAVA_HOME")
  if (s==null) {
    s= "/opt/jdk"
  }
  val dir = new File(s)
  if (!dir.exists) {
    throw new RuntimeException( "No JDK found - try setting 'JAVA_HOME'." )
  }
  Some(dir)
}

name := "a-diagram"

organization := "com.ajjpj.a-diagram"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.2"

scalacOptions ++= List("-deprecation", "-feature")

testOptions in Test += Tests.Argument("-oF")

libraryDependencies ++= Seq(
    "junit" % "junit" % "4.11" % "test",
    "org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test"
)

unmanagedJars in Compile <+= javaHome map { jh /*: Option[File]*/ =>
  val dir: File = jh.getOrElse(null)    // unSome
  //
  val jfxJar = new File(dir, "/jre/lib/jfxrt.jar")
  if (!jfxJar.exists) {
    throw new RuntimeException( "JavaFX not detected (needs Java runtime 7u06 or later): "+ jfxJar.getPath )  // '.getPath' = full filename
  }
  Attributed.blank(jfxJar)
}

//excludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
//  cp filter {_.data.getName == "jfxrt.jar"}
//}

mainClass := Some("com.ajjpj.adiagram.ADiagramMain")

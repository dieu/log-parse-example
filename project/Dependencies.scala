import sbt._

object Version {
  val projectVersion = "0.1.0"

  val scala = "2.11.8"

  val scopt = "3.5.0"

  val scaffeine = "1.2.0"
  val java8compat = "0.8.0-RC1"

  val commonsIo = "2.4"

  val scalaCheck = "1.12.5"
  val scalaTest = "2.2.6"
}

object Library {
  val scopt =  "com.github.scopt" %% "scopt" % Version.scopt

  val scaffeine = "com.github.blemale" %% "scaffeine" % Version.scaffeine % "compile"
  val java8compat = "org.scala-lang.modules" % "scala-java8-compat_2.11" % Version.java8compat

  val commonsIo = "commons-io" % "commons-io" % Version.commonsIo

  val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.scalaCheck % "test"
  val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest % "test"
}

object Dependencies {
  val common = Seq(
    Library.scaffeine,
    Library.java8compat,
    Library.commonsIo
  )

  val cli = Seq(
    Library.scopt
  )

  val test = Seq(
    Library.scalaCheck,
    Library.scalaTest
  )
}
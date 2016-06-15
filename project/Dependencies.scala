import sbt._

object Version {
  val projectVersion = "0.1.0"

  val scala = "2.10.6"

  val scopt = "3.5.0"

  val sparkCore = "1.6.1"
  val sparkSorted = "0.8.0-SNAPSHOT"

  val scalaCheck = "1.12.5"
  val scalaTest = "2.2.6"
  val sscheck = "0.2.4"
  val commonsIo = "2.4"
}

object Library {
  val scopt =  "com.github.scopt" %% "scopt" % Version.scopt

  val sparkCore = "org.apache.spark" %% "spark-core" % Version.sparkCore
  val sparkSorted = "com.tresata" %% "spark-sorted" % Version.sparkSorted
  val commonsIo = "commons-io" % "commons-io" % Version.commonsIo

  val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.scalaCheck % "test"
  val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest % "test"
  val sscheck  ="es.ucm.fdi" %% "sscheck" % Version.sscheck % "test"
}

object Dependencies {
  val spark = Seq(
    Library.sparkCore,
    Library.sparkSorted,
    Library.commonsIo
  )

  val cli = Seq(
    Library.scopt
  )

  val test = Seq(
    Library.scalaCheck,
    Library.scalaTest,
    Library.sscheck
  )
}
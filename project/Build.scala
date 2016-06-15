import sbt._
import Keys._
import sbtassembly.{PathList, MergeStrategy, AssemblyKeys}
import AssemblyKeys._

object SparkExample extends Build {
  lazy val commonSettings = Seq(
    name := "SparkExample",
    organization := "com.example",
    version := Version.projectVersion,
    scalaVersion := Version.scala,
    libraryDependencies ++= Dependencies.cli ++ Dependencies.spark ++ Dependencies.test,
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature"),

    mainClass in (Compile, packageBin) := Some("com.example.spark.StandaloneApp"),
    mainClass in (Compile, run) := Some("com.example.spark.StandaloneApp"),
    mainClass in assembly := Some("com.example.spark.StandaloneApp"),
    assemblyMergeStrategy in assembly := {
      case PathList("javax", "servlet", xs @ _*)              => MergeStrategy.first
      case PathList("META-INF", "MANIFEST.MF")                => MergeStrategy.discard
      case PathList("META-INF", "*.RSA"  | "*.DSA" | "*.SF")  => MergeStrategy.discard
      case "application.conf"                                 => MergeStrategy.concat
      case "unwanted.txt"                                     => MergeStrategy.discard
      case _                                                  => MergeStrategy.first
    }
  )

  lazy val resolversSettings = Seq(
    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      Resolver.sonatypeRepo("public"),
      Resolver.bintrayRepo("juanrh", "maven")
    )
  )

  lazy val root = (project in file("."))
    .settings(commonSettings: _*)
    .settings(resolversSettings: _*)
}

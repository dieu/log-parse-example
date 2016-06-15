package com.example.spark

import es.ucm.fdi.sscheck.gen.RDDGen
import es.ucm.fdi.sscheck.spark.SharedSparkContextBeforeAfterAll
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.scalacheck.{Gen, Prop, Properties}
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

import org.specs2.mutable.Specification
import org.specs2.ScalaCheck
import org.specs2.scalacheck.Parameters

@RunWith(classOf[JUnitRunner])
class LogQuerySpec extends Specification
    with SharedSparkContextBeforeAfterAll
    with ScalaCheck {
  import Prop.forAll

  override def defaultParallelism: Int = 3
  override def sparkMaster : String = "local[5]"
  override def sparkAppName = this.getClass.getName

  implicit def defaultScalacheckParams = Parameters(minTestsOk = 1).verbose

  val currentTime = new java.util.Date().getTime

  def genEvent(firstGen: Gen[String], secondGen: Gen[String]) = for {
    first <- firstGen
    second <- secondGen
    user <- Gen.choose(1, 100)
    begin <- Gen.const(currentTime)
    end <- Gen.const(currentTime + 1)
  } yield Seq(s"$user, $begin, $first", s"$user, $end, $second")

  def genEvents(firstGen: Gen[String], secondGen: Gen[String]): Gen[Seq[String]] = {
    Gen.nonEmptyListOf(genEvent(firstGen, secondGen)).map(_.flatten)
  }

  val openCloseEvents = RDDGen.seqGen2RDDGen(genEvents(Gen.const("open"), Gen.const("close")))
  val openOpenEvents = RDDGen.seqGen2RDDGen(genEvents(Gen.const("open"), Gen.const("open")))
  val closeOpenEvents = RDDGen.seqGen2RDDGen(genEvents(Gen.const("close"), Gen.const("open")))
  val closeCloseEvents = RDDGen.seqGen2RDDGen(genEvents(Gen.const("close"), Gen.const("close")))
  val randomizeEvents = RDDGen.seqGen2RDDGen(genEvents(Gen.oneOf("open", "close"), Gen.oneOf("open", "close")))

  "LogQuery property tests".title ^
    "forall for open close case" ! openClose(sc)
    "forall for open open case" ! openOpen(sc)
    "forall for close close case" ! closeClose(sc)
    "forall for close open case" ! closeOpen(sc)
    "forall for randomize case" ! randomize(sc)

  def openClose(sc: SparkContext) = forAll("dataSet" |: openCloseEvents) { dataSet: RDD[String] =>
    LogQuery
      .query(dataSet, IgnoringPolicy.difference)
      .collect()
      .filterNot({
        case (_, time) => time == 1l
      }) === Array()
  }

  def openOpen(sc: SparkContext) = forAll("dataSet" |: openOpenEvents) { dataSet: RDD[String] =>
    LogQuery
      .query(dataSet, IgnoringPolicy.difference)
      .collect()
      .filterNot({
        case (user, time) => time == 0l
      }) === Array()
  }

  def closeClose(sc: SparkContext) = forAll("dataSet" |: closeCloseEvents) { dataSet: RDD[String] =>
    LogQuery
      .query(dataSet, IgnoringPolicy.difference)
      .collect()
      .filterNot({
        case (user, time) => time == 0l
      }) === Array()
  }

  def closeOpen(sc: SparkContext) = forAll("dataSet" |: closeOpenEvents) { dataSet: RDD[String] =>
    LogQuery
      .query(dataSet, IgnoringPolicy.difference)
      .collect()
      .filterNot({
        case (user, time) => time == 0l
      }) === Array()
  }

  def randomize(sc: SparkContext) = forAll("dataSet" |: randomizeEvents) { dataSet: RDD[String] =>
    LogQuery
      .query(dataSet, IgnoringPolicy.difference)
      .collect()
      .filterNot({
        case (user, time) => time == 0l || time == 1l
      }) === Array()
  }
}

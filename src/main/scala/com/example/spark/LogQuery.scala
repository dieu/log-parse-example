package com.example.spark

import com.tresata.spark.sorted.PairRDDFunctions._
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD

object LogQuery {
  val open = "open"
  val close = "close"

  def query(dataSet: RDD[String], difference: ((Action, Action) => Long)) = {
    dataSet.map(LogQuery.parse)
      .groupSort(Action.ord)
      .mapStreamByKey(LogQuery.calculateTimeSpent(_, difference))
      .mapValues(LogQuery.average)
  }

  def parse(line: String) = {
    line.replace(" ", "").split(",") match {
      case Array(user, time, `open`) => (user, OpenAction(time.toLong))
      case Array(user, time, `close`) => (user, CloseAction(time.toLong))
    }
  }

  def average(timeSpent: (Long, Long)): Long = {
    if (timeSpent._2 == 0) {
      0l
    } else {
      timeSpent._1 / timeSpent._2
    }
  }

  def calculateTimeSpent(values: Iterator[Action],
                         difference: ((Action, Action) => Long)): Iterator[(Long, Long)] = {
    Iterator.single(values.sliding(2).foldLeft((0l, 0l))((acc, actions) => {
      difference(actions.head, actions.last) match {
        case 0l => acc
        case value => (acc._1 + value, acc._2 + 1)
      }
    }))

  }
}

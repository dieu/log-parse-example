package com.example.spark

import java.io.{File, FileOutputStream}
import java.net.InetAddress
import java.nio.charset.Charset
import java.nio.file.{Files, Paths}

import com.example.spark.formatter.PairOutputFormat
import com.example.spark.utils.FileMerger
import org.apache.commons.io.{FilenameUtils, FileUtils}
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.language.implicitConversions

object StandaloneApp {

  case class Config(dataFile: File = new File(classOf[Config].getResource("/log.txt").getPath),
                    outputFolder: File = new File("/tmp/spark_example/"),
                    masterUrl: String = "local[4]",
                    remove: Boolean = true,
                    collect: Boolean = true)

  val outputFile = "output.json"

  val cli = new scopt.OptionParser[Config]("sparkExample") {
    head("spark-example", "0.1.0")

    opt[File]('d', "dataFile").required().valueName("<file>").
      action((x, c) => c.copy(dataFile = x)).
      text("path to file with data")

    opt[File]('o', "output").optional().valueName("<file>").
      action((x, c) => c.copy(outputFolder = x)).
      text("path to folder for results")

    opt[String]('m', "master").optional().valueName("<sparkUrl>").
      action((x, c) => c.copy(masterUrl = x)).
      text("url to spark master")

    opt[Boolean]('r', "remove").optional().valueName("<boolean>").
      action((x, c) => c.copy(remove = x)).
      text("remove output directory")

    opt[Boolean]('c', "collect").optional().valueName("<boolean>").
      action((x, c) => c.copy(collect = x)).
      text("collect result on driver host")

    help("help").text("prints this usage text")

    checkConfig(config => {
      val path = Paths.get(config.outputFolder)
      if (Files.exists(path)) {
        if (config.remove) {
          FileUtils.deleteDirectory(config.outputFolder)
          success
        } else {
          failure("Output folder already exist")
        }
      }
      success
    })

    override def showUsageOnError = true
  }

  implicit def fileToString(file: File): String = file.getAbsolutePath

  def main(args: Array[String]) {
    cli.parse(args, Config()) match {
      case Some(config) =>
        val sparkConf = new SparkConf()
          .setMaster(config.masterUrl)
          .setAppName("SparkExample Log Query")
          .set("spark.driver.host", InetAddress.getLocalHost.getHostAddress)
          .set("spark.local.ip", InetAddress.getLocalHost.getHostAddress)
          .set("spark.blockManager.port", "6000")
          .set("spark.broadcast.factory", "org.apache.spark.broadcast.HttpBroadcastFactory")

        val sc = new SparkContext(sparkConf)

        val dataSet = sc.textFile(config.dataFile, 2)

        val averageSpentDataSet = LogQuery
          .query(dataSet, IgnoringPolicy.difference)
          .cache()

        averageSpentDataSet
          .mapPartitions(_.map(value => (new Text(value._1), new LongWritable(value._2))))
          .saveAsHadoopFile[PairOutputFormat](config.outputFolder)

        if (config.collect) {
          saveOnDriverDisk(config, averageSpentDataSet)
        }

        sc.stop()
      case None =>
    }
  }

  def saveOnDriverDisk(config: Config, dataSet: RDD[(String, Long)]): Unit = {
    val toFile = new FileOutputStream(FilenameUtils.concat(config.outputFolder, outputFile), false)
    FileMerger.using(toFile) { fileWriter =>
      fileWriter.write("[".getBytes)
      var firstRecord = true
      dataSet
        .collect()
        .foreach(data => {
          val (user, time) = data
          if (!firstRecord) {
            fileWriter.write(",".getBytes)
          } else {
            firstRecord = false
          }
          fileWriter.write(s"{$user,$time}".getBytes(Charset.defaultCharset()))
        })
      fileWriter.write("]".getBytes)
    }
  }
}

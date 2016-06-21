package com.example.singleton

import java.io._

import com.example.singleton.cache.{ReadWritePrevalentCacheComponentImpl, CacheContainerComponentImpl}
import com.example.singleton.compute.{ComputeComponentImpl, IgnoringPolicyComponentImpl}
import com.example.singleton.formatter.FormatterComponentImpl
import org.apache.commons.io.FilenameUtils

import scala.language.{implicitConversions, reflectiveCalls}

object StandaloneApp
  extends CacheContainerComponentImpl
  with ReadWritePrevalentCacheComponentImpl
  with FormatterComponentImpl
  with ComputeComponentImpl
  with IgnoringPolicyComponentImpl
  with ParserComponentImpl {

  case class Config(dataFile: File = new File(classOf[Config].getResource("/log.txt").getPath),
                    outputFolder: File = new File("/tmp/singleton_example/"),
                    remove: Boolean = true,
                    size: Int = 1)

  val outputFile = "output.json"

  val cli = new scopt.OptionParser[Config]("sparkExample") {
    head("spark-example", "0.1.0")

    opt[File]('d', "dataFile").optional().valueName("<file>").
      action((x, c) => c.copy(dataFile = x)).
      text("path to file with data")

    opt[File]('o', "output").optional().valueName("<file>").
      action((x, c) => c.copy(outputFolder = x)).
      text("path to folder for results")

    opt[Boolean]('r', "remove").optional().valueName("<boolean>").
      action((x, c) => c.copy(remove = x)).
      text("remove output directory")

    opt[Int]('s', "size").optional().valueName("<size>").
      action((x, c) => c.copy(size = x)).
      text("size of cache in GB")

    help("help").text("prints this usage text")

    checkConfig(config => {
      val path = new File(config.outputFolder)
      if (path.exists()) {
        if (config.remove) {
          for (file <- path.listFiles(new FilenameFilter {
            override def accept(dir: File, name: String): Boolean = name.endsWith(".record") || name.endsWith(".event")
          })) file.delete()
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

  override var cacheSize = 1024l * 1024l

  override var folder = "/tmp/singleton_example/"

  def main(args: Array[String]) {
    cli.parse(args, Config()) match {
      case Some(config) =>
        folder = config.outputFolder
        cacheSize = config.size * 1024l * 1024l * 1024l

        parse(lines(config.dataFile))
          .foreach({
            case (user, action) => compute(user, action)
          })

        readWriteCache.save(folder, cache, new File(FilenameUtils.concat(folder, outputFile)))
      case None =>
    }
  }
}

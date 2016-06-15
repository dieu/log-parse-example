package com.example.spark.utils

import java.io.{File, FileOutputStream}
import java.nio.charset.Charset

import scala.io.Source
import scala.language.reflectiveCalls


object FileMerger {
  def using[A <: {def close(): Unit}, B](param: A)(f: A => B): B = {
    try {
      f(param)
    } finally {
      param.close()
    }
  }

  def merge(folder: File, toFile: File): Unit = {
    using(new FileOutputStream(toFile, false)) { fileWriter =>
      fileWriter.write("[".getBytes)
      var firstRecord = true
      folder
        .listFiles
        .filter(_.getName.startsWith("part-"))
        .foreach(file => {
          for (line <- Source.fromFile(file).getLines()) {
            if (!firstRecord) {
              fileWriter.write(",".getBytes)
            } else {
              firstRecord = false
            }
            fileWriter.write(line.getBytes(Charset.defaultCharset()))
          }
        })
      fileWriter.write("]".getBytes)
    }
  }

}

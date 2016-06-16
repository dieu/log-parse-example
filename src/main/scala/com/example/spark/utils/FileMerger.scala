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
}

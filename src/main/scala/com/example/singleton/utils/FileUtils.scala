package com.example.singleton.utils

import scala.language.reflectiveCalls

object FileUtils {
  def using[A <: {def close() : Unit}, B](param: A)(f: A => B): B = {
    try {
      f(param)
    } finally {
      param.close()
    }
  }
}

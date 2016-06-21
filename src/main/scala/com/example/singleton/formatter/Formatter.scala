package com.example.singleton.formatter

import java.io.{ByteArrayOutputStream, ObjectOutputStream}
import java.nio.charset.StandardCharsets

import com.example.singleton.utils.FileUtils._
import com.example.singleton.{User, UserRecord}

trait Formatter[K, V] {
  def pretty(key: K, value: V): Array[Byte]
  def binary(key: K, value: V): Array[Byte]
}

trait FormatterComponent {
  def formatter[K <: User, V <: UserRecord]: Formatter[K, V]
}

trait FormatterComponentImpl extends FormatterComponent {
  override def formatter[K <: User, V <: UserRecord]: Formatter[K, V] = new FormatterImpl[K, V]()

  class FormatterImpl[K <: User, V <: UserRecord] extends Formatter[K, V] {
    override def pretty(key: K, value: V): Array[Byte] = {
      if (value.n == 0) {
        s"{${key.userId}, ${value.spentTime}}".getBytes(StandardCharsets.UTF_8)
      } else {
        s"{${key.userId}, ${value.spentTime / value.n}}".getBytes(StandardCharsets.UTF_8)
      }
    }

    override def binary(key: K, value: V): Array[Byte] = {
      using(new ByteArrayOutputStream())( stream => {
        new ObjectOutputStream(stream).writeObject(value)
        stream.toByteArray
      })
    }
  }
}
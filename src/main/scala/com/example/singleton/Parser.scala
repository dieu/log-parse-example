package com.example.singleton

import java.io.File

trait ParserComponent {
  def lines(data: File): Iterator[String]
  def parse(lines: Iterator[String]): Iterator[(User, Action)]
}

trait ParserComponentImpl extends ParserComponent {

  val open = "open"
  val close = "close"

  def lines(data: File) = scala.io.Source.fromFile(data).getLines()

  def parse(lines: Iterator[String]) = lines.map(line => {
    line.replace(" ", "").split(",") match {
      case Array(userName, time, `open`) => (User(userName), OpenAction(time.toLong))
      case Array(userName, time, `close`) => (User(userName), CloseAction(time.toLong))
    }
  })
}
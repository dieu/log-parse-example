package com.example.spark

trait DecisionPolicy {
  def difference[A <: Action](one: A, second: A): Long
}

object IgnoringPolicy extends DecisionPolicy {
  override def difference[A <: Action](one: A, second: A): Long = (one, second) match {
    case (open: OpenAction, close: CloseAction) => close - open
    case (open1: OpenAction, open2: OpenAction) => 0l
    case (close: CloseAction, open: OpenAction) => 0l
    case (close1: CloseAction, close2: CloseAction) => 0l
  }
}
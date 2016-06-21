package com.example.singleton


case class User(val userId: String)

case class UserRecord(val spentTime: Long = 0l, val n: Long = 0l, lastAction: Option[Action] = None) extends Serializable


trait Action extends Product with Serializable {
  val timestamp: Long

  def -(that: Action): Long = {
    this.timestamp - that.timestamp
  }
}

object Action {
  implicit val ord: Ordering[Action] = Ordering.by(a => a.timestamp)
}

case class OpenAction(timestamp: Long) extends Action

case class CloseAction(timestamp: Long) extends Action


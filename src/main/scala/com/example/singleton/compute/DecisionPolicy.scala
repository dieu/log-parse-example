package com.example.singleton.compute

import com.example.singleton.{CloseAction, OpenAction, Action}


trait DecisionPolicyComponent {
  def policy: DecisionPolicy

  trait DecisionPolicy {
    /**
      * Function for calculate difference time between event
      *
      * @param one first event
      * @param second second event
      * @tparam A actual event type
      * @return difference time between event
      */
    def difference[A <: Action](one: A, second: A): Operation
  }
}

trait IgnoringPolicyComponentImpl extends DecisionPolicyComponent {
  override def policy: DecisionPolicy = new IgnoringPolicy()

  class IgnoringPolicy extends DecisionPolicy {
    /**
      * Function for calculate difference time between open/close events and ignore others
      *
      * @param one first event
      * @param second second event
      * @tparam A actual event type
      * @return difference time between open/close events and 0l for others
      */
    override def difference[A <: Action](one: A, second: A): Operation = (one, second) match {
      case (open: OpenAction, close: CloseAction) => Store(close - open)
      case (open1: OpenAction, open2: OpenAction) => Continue(open2)
      case (close: CloseAction, open: OpenAction) => Continue(open)
      case (close1: CloseAction, close2: CloseAction) => Skip()
    }
  }
}
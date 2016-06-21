package com.example.spark

import com.example.singleton.compute.{Skip, Continue, Store, IgnoringPolicyComponentImpl}
import com.example.singleton.{CloseAction, OpenAction}
import org.scalacheck._

object IgnoringPolicySpec
  extends Properties("policy")
    with IgnoringPolicyComponentImpl {

  import Prop.forAll

  val currentTime = new java.util.Date().getTime

  val timestamps: Gen[Long] = Gen.choose(1, 99999) map { _ + currentTime}

  implicit def abOpen: Arbitrary[OpenAction] = Arbitrary {
    timestamps map { OpenAction }
  }

  implicit def abClose: Arbitrary[CloseAction] = Arbitrary {
    timestamps map { CloseAction }
  }

  property("computeOpenClose") = forAll { (open: OpenAction, close: CloseAction) =>
    policy.difference(open, close) match {
      case Store(value) => value == close - open
    }
  }

  property("ignoreCloseOpen") = forAll { (close: CloseAction, open: OpenAction) =>
    policy.difference(close, open) match {
      case Continue(`open`) => true
    }
  }

  property("ignoreOpenOpen") = forAll { (first: OpenAction, second: OpenAction) =>
    policy.difference(first, second) match {
      case Continue(`second`) => true
    }
  }

  property("ignoreCloseClose") = forAll { (first: CloseAction, second: CloseAction) =>
    policy.difference(first, second) match {
      case Skip() => true
    }
  }
}

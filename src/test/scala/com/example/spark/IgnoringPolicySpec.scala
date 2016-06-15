package com.example.spark

import org.scalacheck._

object IgnoringPolicySpec extends Properties("policy") {
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
    IgnoringPolicy.difference(open, close) == close - open
  }

  property("ignoreCloseOpen") = forAll { (close: CloseAction, open: OpenAction) =>
    IgnoringPolicy.difference(close, open) == 0l
  }

  property("ignoreOpenOpen") = forAll { (first: OpenAction, second: OpenAction) =>
    IgnoringPolicy.difference(first, second) == 0l
  }

  property("ignoreCloseClose") = forAll { (first: CloseAction, second: CloseAction) =>
    IgnoringPolicy.difference(first, second) == 0l
  }
}

package com.example.spark

import com.example.singleton.cache.CacheContainerComponent
import com.example.singleton.compute.{ComputeComponentImpl, IgnoringPolicyComponentImpl}
import com.example.singleton.{ParserComponentImpl, User, UserRecord}
import com.github.benmanes.caffeine.cache.{CacheLoader, Caffeine, LoadingCache}
import org.scalatest.FlatSpec

import scala.collection.JavaConversions._

object ComputeComponentSpec
  extends FlatSpec
    with ComputeComponentImpl
    with IgnoringPolicyComponentImpl
    with CacheContainerComponent
    with ParserComponentImpl {

  override var cacheSize: Long = 0l
  override var folder: String = ""

  lazy val lazyCache = Caffeine.newBuilder()
    .recordStats()
    .build(new CacheLoader[User, UserRecord]() {
      override def load(key: User): UserRecord = UserRecord()
    })

  override def cache: LoadingCache[User, UserRecord] = lazyCache

  val mixEvents =
    """
      |48, 1466535156182, close
      |48, 1466535156183, close
      |98, 1466535156186, close
      |98, 1466535156187, close
      |96, 1466535156188, close
      |96, 1466535156189, open
      |16, 1466535156189, close
      |16, 1466535156190, close
      |90, 1466535156190, open
      |90, 1466535156191, close
      |14, 1466535156191, close
      |14, 1466535156192, open
      |81, 1466535156191, open
      |81, 1466535156192, open
      |30, 1466535156193, close
      |30, 1466535156194, close
      |99, 1466535156193, open
      |99, 1466535156194, open
      |79, 1466535156194, close
      |79, 1466535156195, open
      |50, 1466535156197, close
      |50, 1466535156198, close
      |15, 1466535156198, open
      |15, 1466535156199, open
      |40, 1466535156199, open
      |40, 1466535156200, close
    """.stripMargin

  "Compute component" should "works with mix events data string" in {
    parse(mixEvents.split("\n").toIterator)
      .foreach({
        case (user, action) => compute(user, action)
      })

    cache.asMap().values().foreach(record => {
      assert(record.spentTime == 0l || record.spentTime / record.n == 1l)
    })

    cache.invalidateAll()
  }
}

package com.example.singleton.cache

import java.io.{BufferedOutputStream, File, FileOutputStream}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import com.example.singleton.formatter.FormatterComponent
import com.example.singleton.utils.FileUtils._
import com.example.singleton.{User, UserRecord}
import com.github.benmanes.caffeine.cache._

import scala.compat.java8.FunctionConverters._

trait CacheContainerComponent {
  var cacheSize: Long

  var folder: String

  def cache: LoadingCache[User, UserRecord]
}

trait ReadWriteCacheComponent {
  def readWriteCache: ReadWriteCache

  trait ReadWriteCache {
    def save(folder: String, from: Cache[User, UserRecord], to: File): Unit

    def writer(folder: String): CacheWriter[User, UserRecord]

    def loader(folder: String): CacheLoader[User, UserRecord]
  }

}

trait CacheContainerComponentImpl extends CacheContainerComponent {
  this: ReadWriteCacheComponent =>

  lazy val lazyCache = Caffeine.newBuilder()
    .maximumSize(cacheSize)
//    .recordStats() %% for debug proposes
    .writer(readWriteCache.writer(folder))
    .build(readWriteCache.loader(folder))

  def cache: LoadingCache[User, UserRecord] = lazyCache
}

trait ReadWritePrevalentCacheComponentImpl extends ReadWriteCacheComponent {
  this: FormatterComponent =>

  override def readWriteCache = new ReadWriteCacheImpl()

  class ReadWriteCacheImpl extends ReadWriteCache {
    val openBrace = "[".getBytes(StandardCharsets.UTF_8)
    val closeBrace = "]".getBytes(StandardCharsets.UTF_8)
    val comma = ",".getBytes(StandardCharsets.UTF_8)

    override def writer(folder: String) = {
      new PrevalentCacheWriter[User, UserRecord](folder, formatter[User, UserRecord])
    }

    override def loader(folder: String) = {
      new PrevalentCacheLoader(folder)
    }

    override def save(folder: String, from: Cache[User, UserRecord], to: File) {
      using(new BufferedOutputStream(new FileOutputStream(to)))(stream => {
        stream.write(openBrace)
        var firstRecord = true

        Files.walk(Paths.get(folder))
          .filter(asJavaPredicate(_.toString.endsWith("record")))
          .forEach(asJavaConsumer(file => {
            if (!firstRecord) {
              stream.write(comma)
            } else {
              firstRecord = false
            }
            Files.copy(file, stream)
            Files.deleteIfExists(file)
            Files.deleteIfExists(file.resolveSibling(file.toFile.getName.replace("record", "event")))
          }))

        stream.write("]".getBytes)
      })
    }
  }

}
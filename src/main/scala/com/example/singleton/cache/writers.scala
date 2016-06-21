package com.example.singleton.cache

import java.io.File

import com.example.singleton.formatter.Formatter
import com.example.singleton.{User, UserRecord}
import com.github.benmanes.caffeine.cache.{CacheWriter, RemovalCause}
import org.apache.commons.io.{FileUtils, FilenameUtils}

class PrevalentCacheWriter[K <: User, V <: UserRecord](val folder: String, val format: Formatter[K, V])
    extends CacheWriter[K, V] {

  override def write(key: K, value: V) {
    val record = new File(FilenameUtils.concat(folder, s"${key.userId}.record"))
    val event = new File(FilenameUtils.concat(folder, s"${key.userId}.event"))

    FileUtils.writeByteArrayToFile(record, format.pretty(key, value))
    FileUtils.writeByteArrayToFile(event, format.binary(key, value))
  }

  override def delete(key: K, value: V, cause: RemovalCause) {}
}
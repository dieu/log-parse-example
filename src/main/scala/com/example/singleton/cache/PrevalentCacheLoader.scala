package com.example.singleton.cache

import com.example.singleton.utils.FileUtils._
import com.example.singleton.{UserRecord, User}

import java.io.{FileInputStream, ObjectInputStream, File}
import com.github.benmanes.caffeine.cache.CacheLoader
import org.apache.commons.io.FilenameUtils

class  PrevalentCacheLoader(val folder: String) extends CacheLoader[User, UserRecord] {
  override def load(key: User): UserRecord = {
    val event = new File(FilenameUtils.concat(folder, s"${key.userId}.event"))

    if (event.exists()) {
      using(new ObjectInputStream(new FileInputStream(event))) { stream =>
        stream.readObject().asInstanceOf[UserRecord]
      }
    } else {
      UserRecord()
    }
  }
}

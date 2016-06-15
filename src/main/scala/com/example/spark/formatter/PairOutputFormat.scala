package com.example.spark.formatter

import java.io.DataOutputStream
import java.nio.charset.Charset

import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapred.TextOutputFormat.LineRecordWriter
import org.apache.hadoop.mapred.{FileOutputFormat, RecordWriter, JobConf, TextOutputFormat}
import org.apache.hadoop.util.Progressable


class PairOutputFormat extends TextOutputFormat[Text, LongWritable] {
  override def getRecordWriter(ignored: FileSystem,
                               job: JobConf,
                               name: String,
                               progress: Progressable): RecordWriter[Text, LongWritable] = {
    val file = FileOutputFormat.getTaskOutputPath(job, name)
    val fs = file.getFileSystem(job)
    val fileOut = fs.create(file, progress)
    new PairRecordWriter(fileOut)
  }

  class PairRecordWriter(out: DataOutputStream) extends LineRecordWriter[Text, LongWritable](out) {
    val newline = "\n".getBytes(Charset.defaultCharset())

    override def write(key: Text, value: LongWritable) = {
      out.write(s"{${key.toString},${value.toString}}".getBytes(Charset.defaultCharset()))
      out.write(newline)
    }
  }
}

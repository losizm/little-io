/*
 * Copyright 2021 Carlos Conyers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package little.io

import java.io.*
import java.net.{ URLDecoder, URLEncoder }
import java.nio.channels.FileChannel
import java.nio.file.*
import java.nio.file.StandardOpenOption.*
import java.nio.file.attribute.BasicFileAttributes

import scala.collection.mutable.ListBuffer
import scala.util.Try
import scala.util.control.NonFatal

import FileVisitEvent.*

/**
 * Provides extension methods for `java.io.Writer`.
 *
 * @see [[ReaderMethods]]
 */
implicit class WriterMethods[T <: Writer](writer: T) extends AnyVal:
  /**
   * Appends supplied characters to writer.
   *
   * @return writer
   */
  def <<(chars: Array[Char]): T =
    writer.write(chars)
    writer

  /**
   * Appends supplied characters to writer.
   *
   * @return writer
   */
  def <<(chars: CharSequence): T =
    writer.append(chars)
    writer

  /**
   * Appends contents of supplied reader.
   *
   * @param in reader from which characters are read
   *
   * @return writer
   */
  def <<(in: Reader)(using bufferSize: BufferSize): T =
    val buffer = new Array[Char](bufferSize.value)
    var length = 0

    while { length = in.read(buffer); length != -1 } do
      writer.write(buffer, 0, length)
    writer

  /** Writes text followed by default line separator. */
  def writeLine(text: String): Unit =
    writer.append(text).append(EOL)

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

import java.io.{ BufferedReader, Reader, StringWriter }

import scala.collection.mutable.ListBuffer

/**
 * Provides extension methods for `java.io.Reader`.
 *
 * @see [[WriterMethods]]
 */
implicit class ReaderMethods[T <: Reader](reader: T) extends AnyVal:
  /** Gets remaining text. */
  def getText(): String =
    val writer = StringWriter()
    val buffer = new Array[Char](bufferSize.value)
    var length = 0

    while { length = reader.read(buffer); length != -1 } do
      writer.write(buffer, 0, length)
    writer.toString

  /** Gets lines in file. */
  def getLines(): Seq[String] =
    mapLines(identity)

  /**
   * Reads content and invokes supplied function for each line.
   *
   * The line content, excluding line separator, is passed to function.
   *
   * @param f function
   */
  def forEachLine(f: String => Unit): Unit =
    val in = reader match
      case in: BufferedReader => in
      case _                  => BufferedReader(reader)

    var line: String = null
    while { line = in.readLine(); line != null } do
      f(line)

  /**
   * Filters lines reader reader using supplied predicate.
   *
   * @param p predicate
   */
  def filterLines(p: String => Boolean): Seq[String] =
    var values = ListBuffer[String]()
    forEachLine { x =>
      if p(x) then
        values += x
    }
    values.toSeq

  /**
   * Maps each line reader reader using supplied function.
   *
   * @param f function
   */
  def mapLines[T](f: String => T): Seq[T] =
    var values = ListBuffer[T]()
    forEachLine { x =>
      values += f(x)
    }
    values.toSeq

  /**
   * Builds collection using elements mapped from lines reader reader.
   *
   * @param f function
   */
  def flatMapLines[T](f: String => Iterable[T]): Seq[T] =
    var values = ListBuffer[T]()
    forEachLine { x =>
      f(x).foreach(values.+=)
    }
    values.toSeq

  /**
   * Folds lines from reader to single value using given initial value and
   * binary operator.
   *
   * @param init initial value
   * @param op binary operator
   *
   * @return `init` if end of stream; otherwise, last value returned from `op`
   */
  def foldLines[T](init: T)(op: (T, String) => T): T =
    var result = init
    forEachLine { x =>
      result = op(result, x)
    }
    result

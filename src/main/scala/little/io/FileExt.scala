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

import scala.util.Try

/**
 * Provides extension methods to `java.io.File`.
 *
 * @see [[PathExt]]
 */
implicit class FileExt(file: File) extends AnyVal:
  /**
   * Creates new file appending child path.
   *
   * @return file
   */
  def /(child: String): File =
    File(file, child)

  /**
   * Appends supplied bytes to file.
   *
   * @return file
   */
  def <<(bytes: Array[Byte]): File =
    withOutputStream(true) { out =>
      out.write(bytes)
      file
    }

  /**
   * Appends supplied characters to file.
   *
   * @return file
   */
  def <<(chars: Array[Char]): File =
    withWriter(true) { out =>
      out.write(chars)
      file
    }

  /**
   * Appends supplied characters to file.
   *
   * @return file
   */
  def <<(chars: CharSequence): File =
    withWriter(true) { out =>
      out.append(chars)
      file
    }

  /**
   * Appends contents of supplied InputStream to file.
   *
   * @return file
   */
  def <<(in: InputStream)(using bufferSize: BufferSize): File =
    withOutputStream(true) { out =>
      out << in
      file
    }

  /**
   * Appends contents of supplied Reader to file.
   *
   * @return file
   */
  def <<(in: Reader)(using bufferSize: BufferSize): File =
    withWriter(true) { out =>
      out << in
      file
    }

  /** Reads file and returns its bytes. */
  def getBytes(): Array[Byte] =
    withInputStream(_.getBytes())

  /** Sets file content to supplied bytes. */
  def setBytes(bytes: Array[Byte]): Unit =
    withOutputStream(_.write(bytes))

  /** Reads file and returns its text. */
  def getText(): String =
    withReader(_.getText())

  /** Sets file content to supplied text. */
  def setText(text: String): Unit =
    withWriter(_.write(text))

  /** Gets lines in file. */
  def getLines(): Seq[String] =
    mapLines(identity)

  /**
   * Reads file and invokes supplied function for each line.
   *
   * The line content, excluding line separator, is passed to function.
   *
   * @param f function
   */
  def forEachLine(f: String => Unit): Unit =
    withReader(_.forEachLine(f))

  /**
   * Filters lines in file using supplied predicate.
   *
   * @param p predicate
   */
  def filterLines(p: String => Boolean): Seq[String] =
    withReader(_.filterLines(p))

  /**
   * Maps each line in file using supplied function.
   *
   * @param f function
   */
  def mapLines[T](f: String => T): Seq[T] =
    withReader(_.mapLines(f))

  /**
   * Builds collection using elements mapped from lines in file.
   *
   * @param f function
   */
  def flatMapLines[T](f: String => Iterable[T]): Seq[T] =
    withReader(_.flatMapLines(f))

  /**
   * Folds lines in file to single value using given initial value and binary
   * operator.
   *
   * @param init initial value
   * @param op binary operator
   *
   * @return `init` if file is empty; otherwise, last value returned from `op`
   */
  def foldLines[T](init: T)(op: (T, String) => T): T =
    withReader(_.foldLines(init)(op))

  /**
   * Invokes supplied function for each file in directory.
   *
   * @param f function
   *
   * @throws java.io.IOException if file is not directory or if directory
   * listing is not available
   */
  def forEachFile(f: File => Unit): Unit =
    file.listFiles match
      case null  => throw FileNotFoundException(s"$file (No directory listing)")
      case files => files.foreach(f)

  /**
   * Maps each file in directory using supplied function.
   *
   * @param f function
   *
   * @throws java.io.IOException if file is not directory or if directory
   * listing is not available
   */
  def mapFiles[T](f: File => T): Seq[T] =
    file.listFiles match
      case null  => throw FileNotFoundException(s"$file (No directory listing)")
      case files => files.toSeq.map(f)

  /**
   * Builds collection using elements mapped from files in directory.
   *
   * @param f function
   *
   * @throws java.io.IOException if file is not directory or if directory
   * listing is not available
   */
  def flatMapFiles[T](f: File => Iterable[T]): Seq[T] =
    file.listFiles match
      case null  => throw FileNotFoundException(s"$file (No directory listing)")
      case files => files.toSeq.flatMap(f)

  /**
   * Folds files in directory to single value using given initial value and
   * binary operator.
   *
   * @param init initial value
   * @param op binary operator
   *
   * @return `init` if no files; otherwise, last value returned from `op`
   *
   * @throws java.io.IOException if file is not directory or if directory
   * listing is not available
   */
  def foldFiles[T](init: T)(op: (T, File) => T): T =
    file.listFiles match
      case null  => throw FileNotFoundException(s"$file (No directory listing)")
      case files => files.foldLeft(init)(op)

  /**
   * Opens InputStream to file and passes it to supplied function. Input
   * stream is closed on function's return.
   *
   * @param f function
   *
   * @return value from supplied function
   */
  def withInputStream[T](f: InputStream => T): T =
    val in = FileInputStream(file)
    try f(in)
    finally Try(in.close())

  /**
   * Opens OutputStream to file and passes it to supplied function. Output
   * stream is closed on function's return.
   *
   * @param f function
   *
   * @return value from supplied function
   */
  def withOutputStream[T](f: OutputStream => T): T =
    withOutputStream(false)(f)

  /**
   * Opens OutputStream to file and passes it to supplied function. Output
   * stream is closed on function's return.
   *
   * @param append if `true`, output is appended to end of file; otherwise, if
   * `false`, file is truncated and output is written at beginning of file
   *
   * @param f function
   *
   * @return value from supplied function
   */
  def withOutputStream[T](append: Boolean)(f: OutputStream => T): T =
    val out = FileOutputStream(file, append)
    try f(out)
    finally Try(out.close())

  /**
   * Opens BufferedReader to file and passes it to supplied function. Reader
   * is closed on function's return.
   *
   * @param f function
   *
   * @return value from supplied function
   */
  def withReader[T](f: BufferedReader => T): T =
    val reader = BufferedReader(FileReader(file))
    try f(reader)
    finally Try(reader.close())

  /**
   * Opens BufferedWriter to file and passes it to supplied function. Writer
   * is closed on function's return.
   *
   * @param f function
   *
   * @return value from supplied function
   */
  def withWriter[T](f: BufferedWriter => T): T =
    withWriter(false)(f)

  /**
   * Opens BufferedWriter to file and passes it to supplied function. Writer
   * is closed on function's return.
   *
   * @param append if `true`, output is appended to end of file; otherwise, if
   * `false`, file is truncated and output is written at beginning of file
   *
   * @param f function
   *
   * @return value from supplied function
   */
  def withWriter[T](append: Boolean)(f: BufferedWriter => T): T =
    val writer = BufferedWriter(FileWriter(file, append))
    try f(writer)
    finally Try(writer.close())

  /**
   * Opens PrintWriter to file and passes it to supplied function. Writer
   * is closed on function's return.
   *
   * @param f function
   *
   * @return value from supplied function
   */
  def withPrintWriter[T](f: PrintWriter => T): T =
    withPrintWriter(false)(f)

  /**
   * Opens PrintWriter to file and passes it to supplied function. Writer
   * is closed on function's return.
   *
   * @param append if `true`, output is appended to end of file; otherwise, if
   * `false`, file is truncated and output is written at beginning of file
   *
   * @param f function
   *
   * @return value from supplied function
   */
  def withPrintWriter[T](append: Boolean)(f: PrintWriter => T): T =
    val writer = PrintWriter(FileWriter(file, append))
    try f(writer)
    finally Try(writer.close())

  /**
   * Opens RandomAccessFile with specified access mode and passes it to
   * function. File is closed on function's return.
   *
   * @param mode access mode
   * @param f function
   *
   * @return value from supplied function
   */
  def withRandomAccess[T](mode: String)(f: RandomAccessFile => T): T =
    val raf = RandomAccessFile(file, mode)
    try f(raf)
    finally Try(raf.close())

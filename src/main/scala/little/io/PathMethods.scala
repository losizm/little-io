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
import java.nio.channels.FileChannel
import java.nio.file.*
import java.nio.file.StandardOpenOption.*
import java.nio.file.attribute.BasicFileAttributes

import scala.collection.mutable.ListBuffer
import scala.util.Try
import scala.util.control.NonFatal

import FileVisitEvent.*

/**
 * Provides extension methods for `java.nio.file.Path`.
 *
 * @see [[FileMethods]]
 */
implicit class PathMethods(path: Path) extends AnyVal:
  /**
   * Creates new file appending child path.
   *
   * @return file
   */
  def /(child: String): Path =
    Paths.get(path.toString, child)

  /**
   * Appends supplied bytes to file.
   *
   * @return path
   */
  def <<(bytes: Array[Byte]): Path =
    withOutputStream(CREATE, APPEND) { out =>
      out.write(bytes)
      path
    }

  /**
   * Appends supplied characters to file.
   *
   * @return path
   */
  def <<(chars: Array[Char]): Path =
    withWriter(CREATE, APPEND) { out =>
      out.write(chars)
      path
    }

  /**
   * Appends supplied characters to file.
   *
   * @return path
   */
  def <<(chars: CharSequence): Path =
    withWriter(CREATE, APPEND) { out =>
      out.append(chars)
      path
    }

  /**
   * Appends contents of supplied InputStream to file.
   *
   * @return path
   */
  def <<(in: InputStream)(using bufferSize: BufferSize): Path =
    withOutputStream(CREATE, APPEND) { out =>
      out << in
      path
    }

  /**
   * Appends contents of supplied Reader to file.
   *
   * @return path
   */
  def <<(in: Reader)(using bufferSize: BufferSize): Path =
    withWriter(CREATE, APPEND) { out =>
      out << in
      path
    }

  /**
   * Appends supplied source to file.
   *
   * @return path
   *
   * @throws IOException if source is same as target
   */
  def <<(source: Path): Path =
    if Files.isSameFile(path, source) then
      throw IOException("Cannot append file to itself")
    source.withInputStream() { in => path << in }

  /** Reads file at path and returns its bytes. */
  def getBytes(): Array[Byte] =
    Files.readAllBytes(path)

  /** Sets file content to supplied bytes. */
  def setBytes(bytes: Array[Byte]): Unit =
    withOutputStream(CREATE, TRUNCATE_EXISTING)(_.write(bytes))

  /** Reads file at path and returns its text. */
  def getText(): String =
    String(getBytes())

  /** Sets file content to supplied text. */
  def setText(text: String): Unit =
    withWriter(CREATE, TRUNCATE_EXISTING)(_.write(text))

  /** Gets lines in file. */
  def getLines(): Seq[String] =
    mapLines(identity)

  /**
   * Reads file at path and invokes supplied function for each line.
   *
   * The line content, excluding line separator, is passed to function.
   *
   * @param f function
   */
  def forEachLine(f: String => Unit): Unit =
    withReader()(_.forEachLine(f))

  /**
   * Filters lines in file using supplied predicate.
   *
   * @param p predicate
   */
  def filterLines(p: String => Boolean): Seq[String] =
    withReader()(_.filterLines(p))

  /**
   * Maps each line in file using supplied function.
   *
   * @param f function
   */
  def mapLines[T](f: String => T): Seq[T] =
    withReader()(_.mapLines(f))

  /**
   * Builds collection using elements mapped from lines in file.
   *
   * @param f function
   */
  def flatMapLines[T](f: String => Iterable[T]): Seq[T] =
    withReader()(_.flatMapLines(f))

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
    withReader()(_.foldLines(init)(op))

  /**
   * Opens directory stream to path and invokes supplied function for each
   * file in directory.
   *
   * @param f function
   *
   * @throws java.io.IOException if path is not to a directory
   */
  def forEachFile(f: Path => Unit): Unit =
    val stream = Files.newDirectoryStream(path)
    try
      stream.forEach(f(_))
    finally
      Try(stream.close())

  /**
   * Invokes supplied function for each file in directory satisfying glob.
   *
   * @param glob glob pattern
   * @param f function
   *
   * @throws java.io.IOException if path is not to a directory
   */
  def forFiles(glob: String)(f: Path => Unit): Unit =
    val stream = Files.newDirectoryStream(path, glob)
    try
      stream.forEach(f(_))
    finally
      Try(stream.close())

  /**
   * Maps each file in directory using supplied function.
   *
   * @param f function
   *
   * @throws java.io.IOException if path is not to a directory
   */
  def mapFiles[T](f: Path => T): Seq[T] =
    foldFiles(new ListBuffer[T]) { (values, file) =>
      values += f(file)
    }.toSeq

  /**
   * Builds collection using elements mapped from files in directory.
   *
   * @param f function
   *
   * @throws java.io.IOException if path is not to a directory
   */
  def flatMapFiles[T](f: Path => Iterable[T]): Seq[T] =
    foldFiles(new ListBuffer[T]) { (values, file) =>
      f(file).foreach(values.+=)
      values
    }.toSeq

  /**
   * Folds files in directory to single value using given initial value and
   * binary operator.
   *
   * @param init initial value
   * @param op binary operator
   *
   * @return `init` if no files; otherwise, last value returned from `op`
   *
   * @throws java.io.IOException if path is not to a directory
   */
  def foldFiles[T](init: T)(op: (T, Path) => T): T =
    var result = init
    forEachFile { x =>
      result = op(result, x)
    }
    result

  /**
   * Walks file tree starting at path and invokes supplied visitor function
   * for each event encountered.
   *
   * If supplied visitor does not handle an event, then it is treated as if
   * it returned `FileVisitResult.CONTINUE`.
   *
   * {{{
   * import java.nio.file.{ FileVisitResult, Paths }
   * import little.io.FileVisitEvent.{ PreVisitDirectory, VisitFile }
   * import little.io.PathMethods
   *
   * val sourceDir = Paths.get("src")
   *
   * sourceDir.withVisitor {
   *   case PreVisitDirectory(dir, attrs) =>
   *     if dir.getFileName.toString == "test" then
   *       FileVisitResult.SKIP_SUBTREE
   *     else
   *       println(s"Listing files in \${dir.getFileName} directory...")
   *       FileVisitResult.CONTINUE
   *
   *   case VisitFile(file, attrs) =>
   *     println(s"\${file.getFileName} is \${attrs.size} bytes.")
   *     FileVisitResult.CONTINUE
   * }
   * }}}
   *
   * @param visitor file visitor
   */
  def withVisitor(visitor: PartialFunction[FileVisitEvent, FileVisitResult]): Unit =
    Files.walkFileTree(path, new FileVisitor[Path] {
      def preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult =
        visitor.applyOrElse(PreVisitDirectory(dir, attrs),
            (evt: FileVisitEvent) => FileVisitResult.CONTINUE)

      def postVisitDirectory(dir: Path, ex: IOException): FileVisitResult =
        visitor.applyOrElse(PostVisitDirectory(dir, Option(ex)),
            (evt: FileVisitEvent) => FileVisitResult.CONTINUE)

      def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult =
        visitor.applyOrElse(VisitFile(file, attrs),
            (evt: FileVisitEvent) => FileVisitResult.CONTINUE)

      def visitFileFailed(file: Path, ex: IOException): FileVisitResult =
        visitor.applyOrElse(VisitFileFailed(file, ex),
            (evt: FileVisitEvent) => FileVisitResult.CONTINUE)
    })

  /**
   * Watchs file at path for specified events.
   *
   * @param events kinds of events to watch
   * @param watcher event watcher
   *
   * @return watch handle
   */
  def withWatcher(events: WatchEvent.Kind[_]*)(watcher: WatchEvent[_] => Unit): WatchHandle =
    val service = path.getFileSystem.newWatchService()

    try
      WatchHandle(service, path.register(service, events*), watcher)
    catch case NonFatal(e) =>
      service.close()
      throw e

  /**
   * Opens InputStream to file at path and passes it to supplied function.
   * Input stream is closed on function's return.
   *
   * @param options open options
   * @param f function
   *
   * @return value from supplied function
   */
  def withInputStream[T](options: OpenOption*)(f: InputStream => T): T =
    val in = Files.newInputStream(path, options*)
    try f(in)
    finally Try(in.close())

  /**
   * Opens OutputStream to file at path and passes it to supplied function.
   * Output stream is closed on function's return.
   *
   * @param options open options
   * @param f function
   *
   * @return value from supplied function
   */
  def withOutputStream[T](options: OpenOption*)(f: OutputStream => T): T =
    val out = Files.newOutputStream(path, options*)
    try f(out)
    finally Try(out.close())

  /**
   * Opens BufferedReader to file at path and passes it to supplied function.
   * Reader is closed on function's return.
   *
   * @param options open options
   * @param f function
   *
   * @return value from supplied function
   */
  def withReader[T](options: OpenOption*)(f: BufferedReader => T): T =
    withInputStream(options*) { in =>
      val reader = BufferedReader(InputStreamReader(in))
      try f(reader)
      finally Try(reader.close())
    }

  /**
   * Opens BufferedWriter to file at path and passes it to supplied function.
   * Writer is closed on function's return.
   *
   * @param options open options
   * @param f function
   *
   * @return value from supplied function
   */
  def withWriter[T](options: OpenOption*)(f: BufferedWriter => T): T =
    val writer = Files.newBufferedWriter(path, options*)
    try f(writer)
    finally Try(writer.close())

  /**
   * Opens PrintWriter to file at path and passes it to supplied function.
   * Writer is closed on function's return.
   *
   * @param options open options
   * @param f function
   *
   * @return value from supplied function
   */
  def withPrintWriter[T](options: OpenOption*)(f: PrintWriter => T): T =
    withOutputStream(options*) { out =>
      val writer = PrintWriter(out)
      try f(writer)
      finally Try(writer.close())
    }

  /**
   * Opens FileChannel to file at path and passes it to supplied function.
   * Channel is closed on function's return.
   *
   * @param options open options
   * @param f function
   *
   * @return value from supplied function
   */
  def withChannel[T](options: OpenOption*)(f: FileChannel => T): T =
    val channel = FileChannel.open(path, options*)
    try f(channel)
    finally Try(channel.close())

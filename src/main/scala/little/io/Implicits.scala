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
import java.util.Base64

import scala.collection.mutable.ListBuffer
import scala.util.Try
import scala.util.control.NonFatal

import FileVisitEvent.*

/** Provides extension methods to `java.io` and `java.nio`. */
object Implicits:
  private val EOL = sys.props("line.separator")
  private val Base64Encoder = Base64.getEncoder()
  private val Base64Decoder = Base64.getDecoder()

  /** Default buffer size for I/O operations &mdash; i.e., BufferSize(8192). */
  given bufferSize: BufferSize = BufferSize(8192)

  /** Provides extension methods to `Array[Byte]`. */
  implicit class IoByteArrayType(bytes: Array[Byte]) extends AnyVal:
    /**
     * Converts bytes to base64 encoded array.
     *
     * @return newly-allocated array with encoded bytes
     */
    def toBase64Encoded: Array[Byte] =
      Base64Encoder.encode(bytes)

    /**
     * Converts bytes to base64 decoded array.
     *
     * @return newly-allocated array with decoded bytes
     */
    def toBase64Decoded: Array[Byte] =
      Base64Decoder.decode(bytes)

  /** Provides extension methods to `String`. */
  implicit class IoStringType(s: String) extends AnyVal:
    /** Converts string to File. */
    def toFile: File = File(s)

    /** Converts string to Path. */
    def toPath: Path = Paths.get(s)

    /**
     * Converts string to URL-encoded value using UTF-8 character encoding.
     *
     * @param charset character encoding
     */
    def toUrlEncoded: String =
      URLEncoder.encode(s, "UTF-8")

    /**
     * Converts string to URL-encoded value using specified character encoding.
     *
     * @param charset character encoding
     */
    def toUrlEncoded(charset: String): String =
      URLEncoder.encode(s, charset)

    /**
     * Converts string to URL-decoded value using UTF-8 character encoding.
     *
     * @param charset character encoding
     */
    def toUrlDecoded: String =
      URLDecoder.decode(s, "UTF-8")

    /**
     * Converts string to URL-decoded value using specified character encoding.
     *
     * @param charset character encoding
     */
    def toUrlDecoded(charset: String): String =
      URLDecoder.decode(s, charset)

  /**
   * Provides extension methods to `java.io.File`.
   *
   * @see [[PathType]]
   */
  implicit class FileType(file: File) extends AnyVal:
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

  /**
   * Provides extension methods to `java.nio.file.Path`.
   *
   * @see [[FileType]]
   */
  implicit class PathType(path: Path) extends AnyVal:
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
     * import little.io.Implicits.PathType
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
     *
     * @see [[FileVisitEvent.PreVisitDirectory PreVisitEvent]], [[FileVisitEvent.PostVisitDirectory PostVisitDirectory]]
     *      [[FileVisitEvent.VisitFile VisitFile]], [[FileVisitEvent.VisitFileFailed VisitFileFailed]]
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

  /**
   * Provides extension methods to `java.io.InputStream`.
   *
   * @see [[OutputStreamType]]
   */
  implicit class InputStreamType[T <: InputStream](in: T) extends AnyVal:
    /** Gets remaining bytes. */
    def getBytes(): Array[Byte] =
      val out = ByteArrayOutputStream()
      val buf = new Array[Byte](bufferSize.value)
      var len = 0

      while { len = in.read(buf); len != -1 } do
        out.write(buf, 0, len)
      out.toByteArray

  /**
   * Provides extension methods to `java.io.OutputStream`.
   *
   * @see [[InputStreamType]]
   */
  implicit class OutputStreamType[T <: OutputStream](out: T) extends AnyVal:
    /**
     * Appends supplied bytes to output stream.
     *
     * @return out
     */
    def <<(bytes: Array[Byte]): T =
      out.write(bytes)
      out

    /**
     * Appends contents of supplied input stream.
     *
     * @param in input stream from which bytes are read
     *
     * @return out
     */
    def <<(in: InputStream)(using bufferSize: BufferSize): T =
      val buf = new Array[Byte](bufferSize.value)
      var len = 0

      while { len = in.read(buf); len != -1 } do
        out.write(buf, 0, len)
      out

  /**
   * Provides extension methods to `java.io.Reader`.
   *
   * @see [[WriterType]]
   */
  implicit class ReaderType[T <: Reader](reader: T) extends AnyVal:
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

  /**
   * Provides extension methods to `java.io.Writer`.
   *
   * @see [[ReaderType]]
   */
  implicit class WriterType[T <: Writer](writer: T) extends AnyVal:
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

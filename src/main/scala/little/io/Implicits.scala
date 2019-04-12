/*
 * Copyright 2018 Carlos Conyers
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

import java.io._
import java.nio.file._
import java.nio.file.StandardOpenOption._
import java.nio.file.attribute._

import scala.collection.GenTraversableOnce
import scala.collection.mutable.ListBuffer
import scala.util.Try
import scala.util.control.NonFatal
import scala.compat.Platform.EOL

import FileVisitEvent._

/** Provides extension methods to {@code java.io} and {@code java.nio}. */
object Implicits {
  /** Default buffer size for I/O operations &mdash; i.e., BufferSize(8192). */
  implicit val bufferSize = BufferSize(8192)

  /**
   * Provides {@code java.io} and {@code java.nio} related extension methods to
   * {@code String}.
   */
  implicit class IoStringType(val s: String) extends AnyVal {
    /** Converts value to File. */
    def toFile: File = new File(s)

    /** Converts value to Path. */
    def toPath: Path = Paths.get(s)
  }

  /**
   * Provides extension methods to {@code java.io.File}.
   *
   * @see [[PathType]]
   */
  implicit class FileType(val file: File) extends AnyVal {
    /**
     * Appends supplied bytes to file.
     *
     * @return file
     */
    def <<(bytes: Array[Byte]): File =
      withOutputStream(true) { out => out.write(bytes); file }

    /**
     * Appends supplied text to file.
     *
     * @return file
     */
    def <<(text: String): File =
      withWriter(true) { out => out.append(text); file }

    /**
     * Appends contents of supplied InputStream to file.
     *
     * @return file
     */
    def <<(in: InputStream)(implicit bufferSize: BufferSize): File =
      withOutputStream(true) { out => out << in; file }

    /**
     * Appends contents of supplied Reader to file.
     *
     * @return file
     */
    def <<(in: Reader)(implicit bufferSize: BufferSize): File =
      withWriter(true) { out => out << in; file }

    /** Reads file and returns its bytes. */
    def getBytes(): Array[Byte] = file.toPath.getBytes

    /** Sets file content to supplied bytes. */
    def setBytes(bytes: Array[Byte]): Unit = file.toPath.setBytes(bytes)

    /** Reads file and returns its text. */
    def getText(): String = file.toPath.getText

    /** Sets file content to supplied text. */
    def setText(text: String): Unit = file.toPath.setText(text)

    /**
     * Reads file content into byte buffer and passes it along with its
     * effective length to supplied function. The function is invoked repeatedly
     * until all content is consumed.
     */
    def readBytes(f: (Array[Byte], Int) => Unit)(implicit bufferSize: BufferSize): Unit =
      withInputStream { in => in.forEach(f) }

    /**
     * Reads file content into character buffer and passes it along with its
     * effective length to supplied function. The function is invoked repeatedly
     * until all content is consumed.
     */
    def readChars(f: (Array[Char], Int) => Unit)(implicit bufferSize: BufferSize): Unit =
      withReader { in => in.forEach(f) }

    /**
     * Reads file and invokes supplied function for each line.
     *
     * The line content, excluding line separator, is passed to function.
     *
     * @param f function
     */
    def forEachLine(f: String => Unit): Unit =
      withReader { in => in.forEachLine(f) }

    /**
     * Filters lines in file using supplied predicate.
     *
     * @param p predicate
     */
    def filterLines(p: String => Boolean): Seq[String] =
      withReader { in => in.filterLines(p) }

    /**
     * Maps each line in file using supplied function.
     *
     * @param f function
     */
    def mapLines[T](f: String => T): Seq[T] =
      withReader { in => in.mapLines(f) }

    /**
     * Builds collection using elements mapped from lines in file.
     *
     * @param f function
     */
    def flatMapLines[T](f: String => GenTraversableOnce[T]): Seq[T] =
      withReader { in => in.flatMapLines(f) }

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
      withReader { in => in.foldLines(init)(op) }

    /**
     * Gets list of files in directory and passes each file to supplied
     * function.
     *
     * @param f function
     *
     * @throws IOException if file is not a directory
     */
    def forEachFile(f: File => Unit): Unit =
      file.toPath.forEachFile("*") { path => f(path.toFile) }

    /**
     * Maps each file in directory using supplied function.
     *
     * @param f function
     *
     * @throws IOException if file is not a directory
     */
    def mapFiles[T](f: File => T): Seq[T] =
      file.toPath.mapFiles { path => f(path.toFile) }

    /**
     * Builds collection using elements mapped from files in directory.
     *
     * @param f function
     *
     * @throws IOException if file is not a directory
     */
    def flatMapFiles[T](f: File => GenTraversableOnce[T]): Seq[T] =
      file.toPath.flatMapFiles { path => f(path.toFile) }

    /**
     * Folds files in directory to single value using given initial value and
     * binary operator.
     *
     * @param init initial value
     * @param op binary operator
     *
     * @return `init` if no files; otherwise, last value returned from `op`
     *
     * @throws IOException if file is not a directory
     */
    def foldFiles[T](init: T)(op: (T, File) => T): T =
      file.toPath.foldFiles(init) { (res, path) => op(res, path.toFile) }

    /**
     * Opens InputStream to file and passes it to supplied function. Input
     * stream is closed on function's return.
     *
     * @param f function
     *
     * @return value from supplied function
     */
    def withInputStream[T](f: InputStream => T): T = {
      val in = new FileInputStream(file)
      try f(in)
      finally Try(in.close())
    }

    /**
     * Opens OutputStream to file and passes it to supplied function. Output
     * stream is closed on function's return.
     *
     * @param f function
     *
     * @return value from supplied function
     */
    def withOutputStream[T](f: OutputStream => T): T = withOutputStream(false)(f)

    /**
     * Opens OutputStream to file and passes it to supplied function. Output
     * stream is closed on function's return.
     *
     * @param append if {@code true}, output is appended to end of file;
     * otherwise, if {@code false}, file is truncated and output is written at
     * beginning of file
     *
     * @param f function
     *
     * @return value from supplied function
     */
    def withOutputStream[T](append: Boolean)(f: OutputStream => T): T = {
      val out = new FileOutputStream(file, append)
      try f(out)
      finally Try(out.close())
    }

    /**
     * Opens BufferedReader to file and passes it to supplied function. Reader
     * is closed on function's return.
     *
     * @param f function
     *
     * @return value from supplied function
     */
    def withReader[T](f: BufferedReader => T): T =
      file.toPath.withReader(f)

    /**
     * Opens BufferedWriter to file and passes it to supplied function. Writer
     * is closed on function's return.
     *
     * @param f function
     *
     * @return value from supplied function
     */
    def withWriter[T](f: BufferedWriter => T): T = withWriter(false)(f)

    /**
     * Opens BufferedWriter to file and passes it to supplied function. Writer
     * is closed on function's return.
     *
     * @param append if {@code true}, output is appended to end of file;
     * otherwise, if {@code false}, file is truncated and output is written at
     * beginning of file
     *
     * @param f function
     *
     * @return value from supplied function
     */
    def withWriter[T](append: Boolean)(f: BufferedWriter => T): T =
      if (append) file.toPath.withWriter(CREATE, APPEND)(f)
      else file.toPath.withWriter(CREATE, TRUNCATE_EXISTING)(f)
  }

  /**
   * Provides extension methods to {@code java.nio.file.Path}.
   *
   * @see [[FileType]]
   */
  implicit class PathType(val path: Path) extends AnyVal {
    /**
     * Appends supplied bytes to file.
     *
     * @return path
     */
    def <<(bytes: Array[Byte]): Path =
      withOutputStream(CREATE, APPEND) { out => out.write(bytes); path }

    /**
     * Appends supplied text to file.
     *
     * @return path
     */
    def <<(text: String): Path =
      withWriter(CREATE, APPEND) { out => out.append(text); path }

    /**
     * Appends contents of supplied InputStream to file.
     *
     * @return path
     */
    def <<(in: InputStream)(implicit bufferSize: BufferSize): Path =
      withOutputStream(CREATE, APPEND) { out => out << in; path }

    /**
     * Appends contents of supplied Reader to file.
     *
     * @return path
     */
    def <<(in: Reader)(implicit bufferSize: BufferSize): Path =
      withWriter(CREATE, APPEND) { out => out << in; path }

    /** Gets size in bytes of file at path. */
    def getSize(): Long = Files.size(path)

    /** Reads file at path and returns its bytes. */
    def getBytes(): Array[Byte] = Files.readAllBytes(path)

    /** Sets file content to supplied bytes. */
    def setBytes(bytes: Array[Byte]): Unit =
      withOutputStream(CREATE, TRUNCATE_EXISTING) { out => out.write(bytes) }

    /** Reads file at path and returns its text. */
    def getText(): String = new String(getBytes())

    /** Sets file content to supplied text. */
    def setText(text: String): Unit =
      withWriter(CREATE, TRUNCATE_EXISTING) { out => out.append(text) }

    /**
     * Reads file content into byte buffer and passes it along with its
     * effective length to supplied function. The function is invoked repeatedly
     * until all content is consumed.
     */
    def readBytes(f: (Array[Byte], Int) => Unit)(implicit bufferSize: BufferSize): Unit =
      withInputStream() { in => in.forEach(f) }

    /**
     * Reads file content into character buffer and passes it along with its
     * effective length to supplied function. The function is invoked repeatedly
     * until all content is consumed.
     */
    def readChars(f: (Array[Char], Int) => Unit)(implicit bufferSize: BufferSize): Unit =
      withReader { in => in.forEach(f) }

    /**
     * Gets owner name of file at path.
     *
     * @param options indicates how symbolic links are handled
     */
    def getOwnerName(options: LinkOption*): String =
      Files.getOwner(path, options : _*).getName

    /**
     * Gets group name of file at path.
     *
     * @param options indicates how symbolic links are handled
     */
    def getGroupName(options: LinkOption*): String =
      Files.getFileAttributeView(path, classOf[PosixFileAttributeView], options : _*)
        .readAttributes().group().getName

    /**
     * Gets file permissions at path.
     *
     * @param options indicates how symbolic links are handled
     */
    def getFilePermissions(options: LinkOption*): FilePermissions =
      FilePermissionsImpl(Files.getPosixFilePermissions(path, options : _*))

    /**
     * Gets creation time of file at path.
     *
     * @param options indicates how symbolic links are handled
     */
    def getCreationTime(options: LinkOption*): FileTime =
      Files.getFileAttributeView(path, classOf[PosixFileAttributeView], options : _*)
        .readAttributes().creationTime()

    /**
     * Gets last access time of file at path.
     *
     * @param options indicates how symbolic links are handled
     */
    def getLastAccessTime(options: LinkOption*): FileTime =
      Files.getFileAttributeView(path, classOf[PosixFileAttributeView], options : _*)
        .readAttributes().lastAccessTime()

    /**
     * Gets last modified time of file at path.
     *
     * @param options indicates how symbolic links are handled
     */
    def getLastModifiedTime(options: LinkOption*): FileTime =
      Files.getLastModifiedTime(path, options : _*)

    /**
     * Tests whether file at path exists.
     *
     * @param options indicates how symbolic links are handled
     */
    def exists(options: LinkOption*): Boolean =
      Files.exists(path, options : _*)

    /**
     * Tests whether file at path does not exist.
     *
     * @param options indicates how symbolic links are handled
     */
    def notExists(options: LinkOption*): Boolean =
      Files.notExists(path, options : _*)

    /**
     * Tests whether file at path is a regular file.
     *
     * @param options indicates how symbolic links are handled
     */
    def isRegularFile(options: LinkOption*): Boolean =
      Files.isRegularFile(path, options : _*)

    /**
     * Tests whether file at path is a directory.
     *
     * @param options indicates how symbolic links are handled
     */
    def isDirectory(options: LinkOption*): Boolean =
      Files.isDirectory(path, options : _*)

    /** Tests whether file at path is a symbolic link. */
    def isSymbolicLink: Boolean = Files.isSymbolicLink(path)

    /** Tests whether file at path is hidden. */
    def isHidden: Boolean = Files.isHidden(path)

    /** Tests whether file at path is readable. */
    def isReadable: Boolean = Files.isReadable(path)

    /** Tests whether file at path is writable. */
    def isWritable: Boolean = Files.isWritable(path)

    /** Tests whether file at path is executable. */
    def isExecutable: Boolean = Files.isExecutable(path)

    /**
     * Reads file at path and invokes supplied function for each line.
     *
     * The line content, excluding line separator, is passed to function.
     *
     * @param f function
     */
    def forEachLine(f: String => Unit): Unit =
      withReader { in => in.forEachLine(f) }

    /**
     * Filters lines in file using supplied predicate.
     *
     * @param p predicate
     */
    def filterLines(p: String => Boolean): Seq[String] =
      withReader { in => in.filterLines(p) }

    /**
     * Maps each line in file using supplied function.
     *
     * @param f function
     */
    def mapLines[T](f: String => T): Seq[T] =
      withReader { in => in.mapLines(f) }

    /**
     * Builds collection using elements mapped from lines in file.
     *
     * @param f function
     */
    def flatMapLines[T](f: String => GenTraversableOnce[T]): Seq[T] =
      withReader { in => in.flatMapLines(f) }

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
      withReader { in => in.foldLines(init)(op) }

    /**
     * Opens directory stream to path and invokes supplied function for each
     * file in directory.
     *
     * @throws IOException if path is not to a directory
     */
    def forEachFile(f: Path => Unit): Unit =
      forEachFile("*")(f)

    /**
     * Opens directory stream to path and invokes supplied function for each
     * file in directory satisfying glob.
     *
     * @throws IOException if path is not to a directory
     */
    def forEachFile(glob: String)(f: Path => Unit): Unit = {
      var stream: DirectoryStream[Path] = null

      try {
        stream = Files.newDirectoryStream(path, glob)
        stream.forEach(f(_))
      } finally Try(stream.close())
    }

    /**
     * Maps each file in directory using supplied function.
     *
     * @param f function
     *
     * @throws IOException if path is not to a directory
     */
    def mapFiles[T](f: Path => T): Seq[T] = {
      var values = new ListBuffer[T]
      path.forEachFile("*") { x => values += f(x) }
      values
    }

    /**
     * Builds collection using elements mapped from files in directory.
     *
     * @param f function
     *
     * @throws IOException if path is not to a directory
     */
    def flatMapFiles[T](f: Path => GenTraversableOnce[T]): Seq[T] = {
      var values = new ListBuffer[T]
      path.forEachFile("*") { x => f(x).foreach(values.+=) }
      values
    }

    /**
     * Folds files in directory to single value using given initial value and
     * binary operator.
     *
     * @param init initial value
     * @param op binary operator
     *
     * @return `init` if no files; otherwise, last value returned from `op`
     *
     * @throws IOException if path is not to a directory
     */
    def foldFiles[T](init: T)(op: (T, Path) => T): T = {
      var result = init
      path.forEachFile("*") { x => result = op(result, x) }
      result
    }

    /**
     * Walks file tree starting at path and invokes supplied visitor function
     * for each event encountered.
     *
     * If supplied visitor does not handle an event, then it is treated as if
     * it returned {@code FileVisitResult.CONTINUE}.
     *
     * {{{
     * import java.nio.file.{ FileVisitResult, Paths }
     * import little.io.FileVisitEvent.{ PreVisitDirectory, VisitFile }
     * import little.io.Implicits.PathType
     *
     * val sourceDir = Paths.get("src")
     *
     * sourceDir.walkFileTree {
     *   case PreVisitDirectory(dir, attrs) ⇒
     *     if (dir.getFileName.toString == "test")
     *       FileVisitResult.SKIP_SUBTREE
     *     else {
     *       println(s"Listing files in ${dir.getFileName} directory...")
     *       FileVisitResult.CONTINUE
     *     }
     *
     *   case VisitFile(file, attrs) ⇒
     *     println(s"${file.getFileName} is ${attrs.size} bytes.")
     *     FileVisitResult.CONTINUE
     * }
     * }}}
     *
     * @see [[FileVisitEvent.PreVisitDirectory PreVisitEvent]], [[FileVisitEvent.PostVisitDirectory PostVisitDirectory]]
     *      [[FileVisitEvent.VisitFile VisitFile]], [[FileVisitEvent.VisitFileFailed VisitFileFailed]]
     */
    def walkFileTree(visitor: PartialFunction[FileVisitEvent, FileVisitResult]): Unit =  {
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
    }

    /**
     * Watch file at path for specified events.
     *
     * @return watch handle
     */
    def watch(events: WatchEvent.Kind[_]*)(watcher: WatchEvent[_] => Unit): WatchHandle = {
      val service = path.getFileSystem.newWatchService()
      try new WatchHandle(service, path.register(service, events : _*), watcher)
      catch { case NonFatal(e) => service.close(); throw e }
    }

    /**
     * Opens InputStream to file at path and passes it to supplied function.
     * Input stream is closed on function's return.
     *
     * @param options options for how to open file
     * @param f function
     *
     * @return value from supplied function
     */
    def withInputStream[T](options: OpenOption*)(f: InputStream => T): T = {
      val in = Files.newInputStream(path, options : _*)
      try f(in)
      finally Try(in.close())
    }

    /**
     * Opens OutputStream to file at path and passes it to supplied function.
     * Output stream is closed on function's return.
     *
     * @param options options for how to open or create file
     * @param f function
     *
     * @return value from supplied function
     */
    def withOutputStream[T](options: OpenOption*)(f: OutputStream => T): T = {
      val out = Files.newOutputStream(path, options : _*)
      try f(out)
      finally Try(out.close())
    }

    /**
     * Opens BufferedReader to file at path and passes it to supplied function.
     * Reader is closed on function's return.
     *
     * @param f function
     *
     * @return value from supplied function
     */
    def withReader[T](f: BufferedReader => T): T = {
      val reader = Files.newBufferedReader(path)
      try f(reader)
      finally Try(reader.close())
    }

    /**
     * Opens BufferedWriter to file at path and passes it to supplied function.
     * Writer is closed on function's return.
     *
     * @param options options for how to open or create file
     * @param f function
     *
     * @return value from supplied function
     */
    def withWriter[T](options: OpenOption*)(f: BufferedWriter => T): T = {
      val out = Files.newBufferedWriter(path, options : _*)
      try f(out)
      finally Try(out.close())
    }
  }

  /**
   * Provides extension methods to {@code java.io.InputStream}.
   *
   * @see [[OutputStreamType]]
   */
  implicit class InputStreamType[T <: InputStream](val in: T) extends AnyVal {
    /** Gets remaining bytes. */
    def getBytes(): Array[Byte] = {
      val out = new ByteArrayOutputStream
      forEach { (buf, len) => out.write(buf, 0, len) }
      out.toByteArray
    }

    /**
     * Reads content into supplied byte buffer and passes it along with its
     * effective length to supplied function. The function is invoked repeatedly
     * until all content is consumed.
     */
    def forEach(buffer: Array[Byte])(f: (Array[Byte], Int) => Unit): Unit = {
      var length = 0
      while ({ length = in.read(buffer); length != -1 })
        f(buffer, length)
    }

    /**
     * Reads content into byte buffer and passes it along with its effective
     * length to supplied function. The function is invoked repeatedly until all
     * content is consumed.
     */
    def forEach(f: (Array[Byte], Int) => Unit)(implicit bufferSize: BufferSize): Unit =
      forEach(new Array[Byte](bufferSize.value))(f)
  }

  /**
   * Provides extension methods to {@code java.io.OutputStream}.
   *
   * @see [[InputStreamType]]
   */
  implicit class OutputStreamType[T <: OutputStream](val out: T) extends AnyVal {
    /**
     * Appends bytes to output stream.
     *
     * @return out
     */
    def <<(bytes: Array[Byte]): T = { out.write(bytes); out }

    /**
     * Appends contents of supplied input stream.
     *
     * @param in input stream from which bytes are read
     *
     * @return out
     */
    def <<(in: InputStream)(implicit bufferSize: BufferSize): T  = {
      val buffer = new Array[Byte](bufferSize.value)
      var length = 0

      while ({ length = in.read(buffer); length != -1 })
        out.write(buffer, 0, length)
      out
    }
  }

  /**
   * Provides extension methods to {@code java.io.Reader}.
   *
   * @see [[WriterType]]
   */
  implicit class ReaderType[T <: Reader](val in: T) extends AnyVal {
    /** Gets remaining text. */
    def getText(): String = {
      val out = new StringBuilder
      forEach { (buf, len) => out.appendAll(buf, 0, len) }
      out.toString
    }

    /**
     * Reads content into supplied character buffer and passes it along with its
     * effective length to supplied function. The function is invoked repeatedly
     * until all content is consumed.
     */
    def forEach(buffer: Array[Char])(f: (Array[Char], Int) => Unit): Unit = {
      var length = 0
      while ({ length = in.read(buffer); length != -1 })
        f(buffer, length)
    }

    /**
     * Reads content into character buffer and passes it along with its
     * effective length to supplied function. The function is invoked repeatedly
     * until all content is consumed.
     */
    def forEach(f: (Array[Char], Int) => Unit)(implicit bufferSize: BufferSize): Unit =
      forEach(new Array[Char](bufferSize.value))(f)

    /**
     * Reads content and invokes supplied function for each line.
     *
     * The line content, excluding line separator, is passed to function.
     *
     * @param f function
     */
    def forEachLine(f: String => Unit): Unit = {
      val reader = in match {
        case in: BufferedReader => in
        case in => new BufferedReader(in)
      }

      var line: String = null
      while ({ line = reader.readLine(); line != null })
        f(line)
    }

    /**
     * Filters lines in reader using supplied predicate.
     *
     * @param p predicate
     */
    def filterLines(p: String => Boolean): Seq[String] = {
      var values = new ListBuffer[String]
      forEachLine { x => if (p(x)) values += x }
      values
    }

    /**
     * Maps each line in reader using supplied function.
     *
     * @param f function
     */
    def mapLines[T](f: String => T): Seq[T] = {
      var values = new ListBuffer[T]
      forEachLine { x => values += f(x) }
      values
    }

    /**
     * Builds collection using elements mapped from lines in reader.
     *
     * @param f function
     */
    def flatMapLines[T](f: String => GenTraversableOnce[T]): Seq[T] = {
      var values = new ListBuffer[T]
      forEachLine { x => f(x).foreach(values.+=) }
      values
    }

    /**
     * Folds lines in reader to single value using given initial value and
     * binary operator.
     *
     * @param init initial value
     * @param op binary operator
     *
     * @return `init` if end of stream; otherwise, last value returned from `op`
     */
    def foldLines[T](init: T)(op: (T, String) => T): T = {
      var result = init
      forEachLine { x => result = op(result, x) }
      result
    }
  }

  /**
   * Provides extension methods to {@code java.io.Writer}.
   *
   * @see [[ReaderType]]
   */
  implicit class WriterType[T <: Writer](val out: T) extends AnyVal {
    /**
     * Appends text to writer.
     *
     * @return out
     */
    def <<(text: String): T = { out.append(text); out }

    /**
     * Appends contents of supplied reader.
     *
     * @param in reader from which characters are read
     *
     * @return out
     */
    def <<(in: Reader)(implicit bufferSize: BufferSize): T = {
      val buffer = new Array[Char](bufferSize.value)
      var length = 0

      while ({ length = in.read(buffer); length != -1 })
        out.write(buffer, 0, length)
      out
    }

    /** Writes text followed by platform's line separator. */
    def writeLine(text: String): Unit =
      out << text << EOL
  }
}

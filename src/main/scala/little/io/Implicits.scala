package little.io

import java.io._
import java.nio.file.{ Files, LinkOption, OpenOption, Path }
import java.nio.file.StandardOpenOption._

import scala.util.Try
import scala.compat.Platform.EOL

/** Provides extension methods to {@code java.io} and {@code java.nio}. */
object Implicits {
  /** Default buffer size for I/O operations &mdash; i.e., BufferSize(8192). */
  implicit val bufferSize = BufferSize(8192)

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
     * Reads file content into byte buffer and passes buffer along with its
     * effective length to supplied function. The function is invoked repeatedly
     * until all content is consumed.
     */
    def forEachByteArray(f: (Array[Byte], Int) => Unit)(implicit bufferSize: BufferSize): Unit =
      withInputStream { in => in.forEachByteArray(f) }

    /**
     * Reads file content into character buffer and passes buffer along with its
     * effective length to supplied function. The function is invoked repeatedly
     * until all content is consumed.
     */
    def forEachCharArray(f: (Array[Char], Int) => Unit)(implicit bufferSize: BufferSize): Unit =
      withReader { in => in.forEachCharArray(f) }

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
      file.toPath.withWriter(if (append) APPEND else TRUNCATE_EXISTING)(f)
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
      withOutputStream(APPEND) { out => out.write(bytes); path }

    /**
     * Appends supplied text to file.
     *
     * @return path
     */
    def <<(text: String): Path =
      withWriter(APPEND) { out => out.append(text); path }

    /**
     * Appends contents of supplied InputStream to file.
     *
     * @return path
     */
    def <<(in: InputStream)(implicit bufferSize: BufferSize): Path =
      withOutputStream(APPEND) { out => out << in; path }

    /**
     * Appends contents of supplied Reader to file.
     *
     * @return path
     */
    def <<(in: Reader)(implicit bufferSize: BufferSize): Path =
      withWriter(APPEND) { out => out << in; path }

    /** Reads file at path and returns its bytes. */
    def getBytes(): Array[Byte] = Files.readAllBytes(path)

    /** Sets file content to supplied bytes. */
    def setBytes(bytes: Array[Byte]): Unit =
      withOutputStream(TRUNCATE_EXISTING) { out => out.write(bytes) }

    /** Reads file at path and returns its text. */
    def getText(): String = new String(getBytes())

    /** Sets file content to supplied text. */
    def setText(text: String): Unit =
      withWriter(TRUNCATE_EXISTING) { out => out.append(text) }

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
     * Reads file content into byte buffer and passes buffer along with its
     * effective length to supplied function. The function is invoked repeatedly
     * until all content is consumed.
     */
    def forEachByteArray(f: (Array[Byte], Int) => Unit)(implicit bufferSize: BufferSize): Unit =
      withInputStream() { in => in.forEachByteArray(f) }

    /**
     * Reads file content into character buffer and passes buffer along with its
     * effective length to supplied function. The function is invoked repeatedly
     * until all content is consumed.
     */
    def forEachCharArray(f: (Array[Char], Int) => Unit)(implicit bufferSize: BufferSize): Unit =
      withReader { in => in.forEachCharArray(f) }

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
    /**
     * Reads content into byte buffer and passes buffer along with its effective
     * length to supplied function. The function is invoked repeatedly until all
     * content is consumed.
     */
    def forEachByteArray(f: (Array[Byte], Int) => Unit)(implicit bufferSize: BufferSize): Unit = {
      val buf = new Array[Byte](bufferSize.value)
      var len = 0
      while ({ len = in.read(buf); len != -1 })
        f(buf, len)
    }
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
    /**
     * Reads content into character buffer and passes buffer along with its
     * effective length to supplied function. The function is invoked repeatedly
     * until all content is consumed.
     */
    def forEachCharArray(f: (Array[Char], Int) => Unit)(implicit bufferSize: BufferSize): Unit = {
      val buf = new Array[Char](bufferSize.value)
      var len = 0
      while ({ len = in.read(buf); len != -1 })
        f(buf, len)
    }

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

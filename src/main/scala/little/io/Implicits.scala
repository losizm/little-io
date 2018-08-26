package little.io

import java.io._
import java.nio.file.{ Files, OpenOption, Path }
import java.nio.file.StandardOpenOption._

import scala.util.Try

/** Provides extension methods to {@code java.io} and {@code java.nio}. */
object Implicits {
  /** Default buffer size for I/O operations &mdash; i.e., BufferSize(8192). */
  implicit val bufferSize = BufferSize(8192)

  /** Provides extension methods to {@code java.io.File}. */
  implicit class FileType(val file: File) extends AnyVal {
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
     * Appends supplied text to file.
     *
     * @return file
     */
    def <<(text: String): File =
      withWriter(true) { out =>
        out.append(text)
        file
      }

    /**
     * Appends contents of supplied InputStream to file.
     *
     * @return file
     */
    def <<(in: InputStream)(implicit bufferSize: BufferSize): File =
      withOutputStream(true) { out =>
        out << in
        file
      }

    /**
     * Appends contents of supplied Reader to file.
     *
     * @return file
     */
    def <<(in: Reader)(implicit bufferSize: BufferSize): File =
      withWriter(true) { out =>
        out << in
        file
      }

    /** Reads file and returns byte array. */
    def getBytes(): Array[Byte] = file.toPath.getBytes

    /** Sets file content to supplied bytes. */
    def setBytes(bytes: Array[Byte]): Unit = file.toPath.setBytes(bytes)

    /** Reads file and returns text. */
    def getText(): String = file.toPath.getText

    /** Sets file content to supplied text. */
    def setText(text: String): Unit = file.toPath.setText(text)

    /**
     * Reads file and invokes supplied function for each line in file.
     *
     * The line content, excluding line separator, is passed to function.
     *
     * @param f function
     */
    def forEachLine(f: String => Unit): Unit = file.toPath.forEachLine(f)

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
     * @param append if {@code true}, output is written at end of file;
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
     * @param append if {@code true}, output is written at end of file;
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

  /** Provides extension methods to {@code java.nio.file.Path}. */
  implicit class PathType(val path: Path) extends AnyVal {
    /**
     * Appends supplied bytes to file.
     *
     * @return path
     */
    def <<(bytes: Array[Byte]): Path =
      withOutputStream(APPEND) { out =>
        out.write(bytes)
        path
      }

    /**
     * Appends supplied text to file.
     *
     * @return path
     */
    def <<(text: String): Path =
      withWriter(APPEND) { out =>
        out.append(text)
        path
      }

    /**
     * Appends contents of supplied InputStream to file.
     *
     * @return path
     */
    def <<(in: InputStream)(implicit bufferSize: BufferSize): Path =
      withOutputStream(APPEND) { out =>
        out << in
        path
      }

    /**
     * Appends contents of supplied Reader to file.
     *
     * @return path
     */
    def <<(in: Reader)(implicit bufferSize: BufferSize): Path =
      withWriter(APPEND) { out =>
        out << in
        path
      }

    /** Reads file at path and returns byte array. */
    def getBytes(): Array[Byte] = Files.readAllBytes(path)

    /** Sets file content to supplied bytes. */
    def setBytes(bytes: Array[Byte]): Unit =
      withOutputStream(TRUNCATE_EXISTING) { out => out.write(bytes) }

    /** Reads file at path and returns text. */
    def getText(): String = new String(getBytes())

    /** Sets file content to supplied text. */
    def setText(text: String): Unit =
      withWriter(TRUNCATE_EXISTING) { out => out.append(text) }

    /**
     * Reads file at path and invokes supplied function for each line in file.
     *
     * The line content, excluding line separator, is passed to function.
     *
     * @param f function
     */
    def forEachLine(f: String => Unit): Unit =
      withReader { in =>
        var line: String = null
        while ({ line = in.readLine(); line != null })
          f(line)
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

  /** Provides extension methods to {@code java.io.OutputStream}. */
  implicit class OutputStreamType[T <: OutputStream](val out: T) extends AnyVal {
    /**
     * Writes all bytes from given input stream.
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

  /** Provides extension methods to {@code java.io.Writer}. */
  implicit class WriterType[T <: Writer](val out: T) extends AnyVal {
    /**
     * Writes all characters from given reader.
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
  }
}

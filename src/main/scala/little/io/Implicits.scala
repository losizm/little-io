package little.io

import java.io._
import java.nio.file.{ Files, OpenOption, Path }

import scala.util.Try

/** Provides extension methods to {@code java.io} and {@code java.nio}. */
object Implicits {
  /** Default buffer size for I/O operations &mdash; i.e., BufferSize(8192). */
  implicit val bufferSize = BufferSize(8192)

  /** Provides extension methods to {@code java.io.File}. */
  implicit class FileType(val file: File) extends AnyVal {
    /** Reads file and returns byte array. */
    def getBytes(): Array[Byte] = Files.readAllBytes(file.toPath)

    /** Reads file and returns text. */
    def getText(): String = new String(getBytes())

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
     * Opens Reader to file and passes it to supplied function. Reader is closed
     * on function's return.
     *
     * @param f function
     *
     * @return value from supplied function
     */
    def withReader[T](f: Reader => T): T = {
      val reader = new FileReader(file)
      try f(reader)
      finally Try(reader.close())
    }

    /**
     * Opens Writer to file and passes it to supplied function. Writer is closed
     * on function's return.
     *
     * @param f function
     *
     * @return value from supplied function
     */
    def withWriter[T](f: Writer => T): T = withWriter(false)(f)

    /**
     * Opens Writer to file and passes it to supplied function. Writer is closed
     * on function's return.
     *
     * @param append if {@code true}, output is written at end of file;
     * otherwise, if {@code false}, file is truncated and output is written at
     * beginning of file
     *
     * @param f function
     *
     * @return value from supplied function
     */
    def withWriter[T](append: Boolean)(f: Writer => T): T = {
      val writer = new FileWriter(file, append)
      try f(writer)
      finally Try(writer.close())
    }
  }

  /** Provides extension methods to {@code java.nio.file.Path}. */
  implicit class PathType(val path: Path) extends AnyVal {
    /** Reads file at path and returns byte array. */
    def getBytes(): Array[Byte] = Files.readAllBytes(path)

    /** Reads file at path and returns text. */
    def getText(): String = new String(getBytes())

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

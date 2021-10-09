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

import java.io.{ File, FileFilter, InputStream, OutputStream }
import java.nio.file.{ Files, FileVisitResult, Path, Paths, PathMatcher }
import java.nio.file.StandardOpenOption.*
import java.util.zip.*

import scala.util.Try

import FileVisitEvent.*

/** Includes compression methods. */
object Compressor:
  /**
   * Gzips input file to output file.
   *
   * @param in input file
   * @param out output file
   * @param bufferSize buffer size used in I/O operations
   */
  def gzip(in: File, out: File)(using bufferSize: BufferSize): Unit =
    gzip(in.toPath, out.toPath)

  /**
   * Gzips input file to output file.
   *
   * @param in input file
   * @param out output file
   * @param bufferSize buffer size used in I/O operations
   */
  def gzip(in: Path, out: Path)(using bufferSize: BufferSize): Unit =
    in.withInputStream() { inStream =>
      out.withOutputStream() { outStream =>
        gzip(inStream, outStream)
      }
    }

  /**
   * Gzips input stream to output stream.
   *
   * @param in input stream
   * @param out output stream
   * @param bufferSize buffer size used in I/O operations
   *
   * @note This method does not close input and output streams.
   */
  def gzip(in: InputStream, out: OutputStream)(using bufferSize: BufferSize): Unit =
    val deflate = GZIPOutputStream(out)
    deflate << in
    deflate.finish()
    deflate.flush()

  /**
   * Gunzips input file to output file.
   *
   * @param in input file
   * @param out output file
   * @param bufferSize buffer size used in I/O operations
   */
  def gunzip(in: File, out: File)(using bufferSize: BufferSize): Unit =
    gunzip(in.toPath, out.toPath)

  /**
   * Gunzips input file to output file.
   *
   * @param in input file
   * @param out output file
   * @param bufferSize buffer size used in I/O operations
   */
  def gunzip(in: Path, out: Path)(using bufferSize: BufferSize): Unit =
    in.withInputStream() { inStream =>
      out.withOutputStream() { outStream =>
        gunzip(inStream, outStream)
      }
    }

  /**
   * Gunzips input stream to output stream.
   *
   * @param in input stream
   * @param out output stream
   * @param bufferSize buffer size used in I/O operations
   *
   * @note This method does not close input and output streams.
   */
  def gunzip(in: InputStream, out: OutputStream)(using bufferSize: BufferSize): Unit =
    val inflate = GZIPInputStream(in)
    out << inflate
    out.flush()

  /**
   * Deflates input file to output file.
   *
   * @param in input file
   * @param out output file
   * @param bufferSize buffer size used in I/O operations
   */
  def deflate(in: File, out: File)(using bufferSize: BufferSize): Unit =
    deflate(in.toPath, out.toPath)

  /**
   * Deflates input file to output file.
   *
   * @param in input file
   * @param out output file
   * @param bufferSize buffer size used in I/O operations
   */
  def deflate(in: Path, out: Path)(using bufferSize: BufferSize): Unit =
    in.withInputStream() { inStream =>
      out.withOutputStream() { outStream =>
        deflate(inStream, outStream)
      }
    }

  /**
   * Deflates input stream to output stream.
   *
   * @param in input stream
   * @param out output stream
   * @param bufferSize buffer size used in I/O operations
   *
   * @note This method does not close input and output streams.
   */
  def deflate(in: InputStream, out: OutputStream)(using bufferSize: BufferSize): Unit =
    val deflate = DeflaterOutputStream(out)
    deflate << in
    deflate.finish()
    deflate.flush()

  /**
   * Inflates input file to output file.
   *
   * @param in input file
   * @param out output file
   * @param bufferSize buffer size used in I/O operations
   */
  def inflate(in: File, out: File)(using bufferSize: BufferSize): Unit =
    inflate(in.toPath, out.toPath)

  /**
   * Inflates input file to output file.
   *
   * @param in input file
   * @param out output file
   * @param bufferSize buffer size used in I/O operations
   */
  def inflate(in: Path, out: Path)(using bufferSize: BufferSize): Unit =
    in.withInputStream() { inStream =>
      out.withOutputStream() { outStream =>
        inflate(inStream, outStream)
      }
    }

  /**
   * Inflates input stream to output stream.
   *
   * @param in input stream
   * @param out output stream
   * @param bufferSize buffer size used in I/O operations
   *
   * @note This method does not close input and output streams.
   */
  def inflate(in: InputStream, out: OutputStream)(using bufferSize: BufferSize): Unit =
    val inflate = InflaterInputStream(in)
    out << inflate
    out.flush()

  /**
   * Zips all files in input directory (recursive) to output file.
   *
   * @param in input directory
   * @param out output file
   * @param filter file filter
   */
  def zip(in: File, out: File)(using filter: FileFilter): Unit =
    zip(in.toPath, out.toPath)(using path => filter.accept(path.toFile))

  /**
   * Zips all files in input directory (recursive) to output file.
   *
   * @param in input directory
   * @param out output file
   * @param matcher path matcher
   */
  def zip(in: Path, out: Path)(using matcher: PathMatcher): Unit =
    out.withOutputStream(CREATE, TRUNCATE_EXISTING) { outStream =>
      val zipStream = ZipOutputStream(outStream)

      try
        in.withVisitor {
          case PreVisitDirectory(dir, _) =>
            matcher.matches(dir) match
              case true  => FileVisitResult.CONTINUE
              case false => FileVisitResult.SKIP_SUBTREE

          case VisitFile(file, _) =>
            if matcher.matches(file) then
              file.withInputStream() { inStream =>
                zipStream.putNextEntry(ZipEntry(in.relativize(file).toString))
                zipStream << inStream
                zipStream.flush()
                zipStream.closeEntry()
              }
            FileVisitResult.CONTINUE
        }

        zipStream.finish()
      finally
        Try(zipStream.close())
    }

  /**
   * Unzips input file to output directory.
   *
   * @param in input file
   * @param out output directory
   * @param filter file filter
   */
  def unzip(in: File, out: File)(using filter: FileFilter): Unit =
    unzip(in.toPath, out.toPath)(using path => filter.accept(path.toFile))

  /**
   * Unzips input file to output directory.
   *
   * @param in input file
   * @param out output directory
   * @param matcher path matcher
   */
  def unzip(in: Path, out: Path)(using matcher: PathMatcher): Unit =
    createDirectory(out)

    in.withInputStream() { inStream =>
      val zipStream = ZipInputStream(inStream)
      var entry: ZipEntry = null
      var path: Path = null

      while { entry = zipStream.getNextEntry(); entry != null } do
        path = Paths.get(entry.getName)

        if !entry.isDirectory && matcher.matches(path) then
          path = out.resolve(path)
          createDirectories(path.getParent)

          path.withOutputStream(CREATE, TRUNCATE_EXISTING) { outStream =>
            outStream << zipStream
            outStream.flush()
          }

        zipStream.closeEntry()
    }

  private def createDirectory(path: Path): Unit =
    if !Files.isDirectory(path) then
      Files.createDirectory(path)

  private def createDirectories(path: Path): Unit =
    if !Files.isDirectory(path) then
      Files.createDirectories(path)

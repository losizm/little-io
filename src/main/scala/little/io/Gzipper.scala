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

import java.io.{ File, InputStream, OutputStream }
import java.nio.file.Path
import java.util.zip.{ GZIPInputStream, GZIPOutputStream }

import Implicits.{ OutputStreamType, PathType }

/** Includes methods to gzip and gunzip data. */
object Gzipper {
  /** Gzips input file to output file. */
  def gzip(in: File, out: File)(implicit bufferSize: BufferSize): Unit =
    gzip(in.toPath, out.toPath)

  /** Gzips input file to output file. */
  def gzip(input: Path, output: Path)(implicit bufferSize: BufferSize): Unit =
    input.withInputStream() { in =>
      output.withOutputStream() { out =>
        gzip(in, out)
      }
    }

  /**
   * Gzips input stream to output stream.
   *
   * <strong>Note:</strong> This method closes neither `in` nor `out`.
   */
  def gzip(in: InputStream, out: OutputStream)(implicit buffersSize: BufferSize): Unit = {
    val deflate = new GZIPOutputStream(out)
    deflate << in
    deflate.finish()
    deflate.flush()
  }

  /** Gunzips input file to output file. */
  def gunzip(input: File, output: File)(implicit bufferSize: BufferSize): Unit =
    gunzip(input.toPath, output.toPath)

  /** Gunzips input file to output file. */
  def gunzip(input: Path, output: Path)(implicit bufferSize: BufferSize): Unit =
    input.withInputStream() { in =>
      output.withOutputStream() { out =>
        gunzip(in, out)
      }
    }

  /**
   * Gunzips input stream to output stream.
   *
   * <strong>Note:</strong> This method closes neither `in` nor `out`.
   */
  def gunzip(in: InputStream, out: OutputStream)(implicit buffersSize: BufferSize): Unit = {
    val inflate = new GZIPInputStream(in)
    out << inflate
    out.flush()
  }
}

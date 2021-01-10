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

import java.util.zip.ZipFile

import Compressor._
import Implicits.FileType
import TestFile._

class CompressorSpec extends org.scalatest.flatspec.AnyFlatSpec {
  val text = "Now Peter Piper picked peppers\nbut Run rocks rhymes."

  "File" should "be gzipped and gunzipped" in {
    implicit val bufferSize = BufferSize(32)

    val in = createTempFile() << text
    val gzipped = createTempFile()
    val gunzipped = createTempFile()

    gzip(in, gzipped)
    assert(in.getText() != gzipped.getText())

    gunzip(gzipped, gunzipped)
    assert(in.getText() == gunzipped.getText())
  }

  it should "be deflated and inflated" in {
    implicit val bufferSize = BufferSize(32)

    val in = createTempFile() << text
    val deflated = createTempFile()
    val inflated = createTempFile()

    deflate(in, deflated)
    assert(in.getText() != deflated.getText())

    inflate(deflated, inflated)
    assert(in.getText() == inflated.getText())
  }

  it should "be zipped and unzipped" in {
    implicit val filter = AcceptAnyFile

    val src = createTempDir()
    val in = createTempFile(src) << text
    val zipped = createTempFile(suffix = ".zip")
    val unzipped = createTempDir()

    zip(src, zipped)
    val zipFile = new ZipFile(zipped)

    try assert(zipFile.size == 1)
    finally zipFile.close()

    unzip(zipped, unzipped)
    unzipped.forEachFile(f => assert(f.getText() == in.getText()))
  }
}

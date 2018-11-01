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

import org.scalatest.FlatSpec

import Gzipper._
import Implicits.FileType
import TestFile._

class GzipperSpec extends FlatSpec {
  val text = "Now Peter Piper picked peppers\nbut Run rocks rhymes."

  "Data" should "be gzipped and gunzipped" in {
    implicit val bufferSize = BufferSize(32)

    val in = createTempFile() << text
    val gzipped = createTempFile()
    val gunzipped = createTempFile()

    gzip(in, gzipped)
    assert(in.getText != gzipped.getText)

    gunzip(gzipped, gunzipped)
    assert(in.getText == gunzipped.getText)
  }
}

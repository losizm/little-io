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

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

import org.scalatest.FlatSpec

import Implicits._

class InputStreamTypeSpec extends FlatSpec {
  "InputStream" should "read bytes into buffer" in {
    implicit val bufferSize = BufferSize(8)

    val text = "Now Peter Piper picked peppers but Run rocks rhymes."
    val in = new ByteArrayInputStream(text.getBytes)
    val out = new ByteArrayOutputStream()

    in.forEach { (buf, len) => out.write(buf, 0, len) }

    assert(out.toString == text)
  }

  it should "read all bytes" in {
    val text = "Now Peter Piper picked peppers but Run rocks rhymes."
    val in = new ByteArrayInputStream(text.getBytes)
    assert(new String(in.getBytes) == text)
  }
}

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

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream, StringReader, StringWriter }

import org.scalatest.FlatSpec

import Implicits._
import TestFile._

class FileTypeSpec extends FlatSpec {
  implicit val bufferSize = BufferSize(64)

  val text = "Now Peter Piper picked peppers\nbut Run rocks rhymes."

  "File" should "be written to output stream and read from input stream" in {
    val bytes = text.getBytes("utf-8")
    val file = createTempFile()
    
    file.withOutputStream(out => out.write(bytes))

    file.withInputStream { in =>
      val out = new ByteArrayOutputStream() << in
      assert(out.toByteArray.corresponds(bytes)(_ == _))
    }

    assert(file.getBytes.corresponds(bytes)(_ == _))
  }

  it should "be written to writer and read from reader" in {
    val file = createTempFile()
    
    file.withWriter(writer => writer.append(text))

    file.withReader { reader =>
      val writer = new StringWriter() << reader
      assert(writer.toString == text)
    }

    assert(file.getText == text)

    file.forEachLine { (line, number) =>
      if (number == 1) assert(line == text.split("\n").head)
      else assert(line == text.split("\n").last)
    }
  }

  it should "have its content set to bytes and text" in {
    val file = createTempFile()

    file.setText("abc")
    file << "123".getBytes << ".!?"

    assert(file.getText == "abc123.!?")

    file.setBytes("ABC".getBytes)
    file << "123" << ".!?".getBytes << new ByteArrayInputStream("abc".getBytes) << new StringReader("xyz")

    assert(file.getText == "ABC123.!?abcxyz")
  }
}

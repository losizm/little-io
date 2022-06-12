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

import java.io.*

import TestFile.*

class FileMethodsSpec extends org.scalatest.flatspec.AnyFlatSpec:
  given BufferSize = BufferSize(64)

  val text = "Now Peter Piper picked peppers\nbut Run rocks rhymes."

  "File" should "be written to output stream and read from input stream" in {
    val bytes = text.getBytes("utf-8")
    val file = createTempFile()

    file.withOutputStream(out => out.write(bytes))

    file.withInputStream { in =>
      val out = ByteArrayOutputStream() << in
      assert(out.toByteArray.corresponds(bytes)(_ == _))
    }

    assert(file.getBytes().corresponds(bytes)(_ == _))
  }

  it should "be written to writer and read from reader" in {
    val file = createTempFile()

    file.withWriter(writer => writer.append(text))

    file.withReader { reader =>
      val writer = StringWriter() << reader
      assert(writer.toString == text)
    }

    assert(file.getText() == text)

    file.forEachLine { line =>
      assert(text.split("\n").contains(line))
    }
  }

  it should "write to data output and read from data input" in {
    val file = createTempFile()

    val result1 = file.withDataOutput { output =>
      output.writeBoolean(true)
      output.writeInt(45)
      output.writeUTF("Hello, world!!!")
      true
    }

    assert(result1)

    val result2 = file.withDataOutput(true) { output =>
      output.writeDouble(123.789)
      200
    }

    assert(result2 == 200)

    val result3 = file.withDataInput { input =>
      (input.readBoolean(), input.readInt(), input.readUTF(), input.readDouble())
    }

    assert(result3 == (true, 45, "Hello, world!!!", 123.789))
  }

  it should "be written to and read from random access file" in {
    val bytes = text.getBytes("utf-8")
    val file = createTempFile()

    file.withRandomAccess("rw") { f => f.write(bytes) }

    val chars = new Array[Byte](bytes.length)
    file.withRandomAccess("r") { f => f.read(chars) }

    assert(String(chars, "utf-8") == text)
  }

  it should "have its content set to bytes and text" in {
    val file = createTempFile()

    file.setText("abc")
    file << "123".getBytes << ".!?"

    assert(file.getText() == "abc123.!?")

    file.setBytes("ABC".getBytes)
    file << "123"
    file << ".!?".getBytes
    file << ByteArrayInputStream("abc".getBytes)
    file << StringReader("xyz")
    file << "foobarbaz".toCharArray

    assert(file.getText() == "ABC123.!?abcxyzfoobarbaz")
  }

  it should "have its lines filtered, mapped, and folded" in {
    val file = createTempFile()

    file.setText("abc\n123\nxyz\n789")

    assert { file.getLines() == Seq("abc", "123", "xyz", "789") }

    val filter = file.filterLines(_.matches("\\d+"))
    assert { filter.sameElements(Seq("123", "789")) }

    val map = file.mapLines(_.toUpperCase)
    assert { map.sameElements(Seq("ABC", "123", "XYZ", "789")) }

    val flatMap = file.flatMapLines { line =>
      line.matches("[a-z]+") match {
        case true  => Some(line)
        case false => None
      }
    }
    assert { flatMap.sameElements(Seq("abc", "xyz")) }

    val fold = file.foldLines("") { _ + _ }
    assert(fold == "abc123xyz789")
  }

  it should "be created with a child path" in {
    val f = File("/a/b/c")

    assert { f / "x" / "y" / "z" == File("/a/b/c/x/y/z") }

    assert { (f / "/x" / "/y" / "/z").getCanonicalFile == File("/a/b/c/x/y/z") }
    assert { (f / "../x" / "/y" / "/z").getCanonicalFile == File("/a/b/x/y/z") }
    assert { (f / "../x" / "../y" / "/z").getCanonicalFile == File("/a/b/y/z") }
    assert { (f / "../x" / "../y" / "../z").getCanonicalFile == File("/a/b/z") }
  }

  it should "be append with another file" in {
    val source = createTempFile() << "Hello, world!"
    val target = createTempFile() << source

    assert(target.getText() == "Hello, world!")

    target << source
    assert(target.getText() == "Hello, world!" * 2)

    assertThrows[IOException](target << target)
  }

  "PrintWriter" should "be created from file" in {
    val file = createTempFile()

    file.withPrintWriter(writer => writer.print(text))

    file.withReader { reader =>
      val writer = StringWriter() << reader
      assert(writer.toString == text)
    }

    assert(file.getText() == text)

    file.forEachLine { line =>
      assert(text.split("\n").contains(line))
    }
  }

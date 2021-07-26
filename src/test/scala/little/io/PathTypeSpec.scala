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

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream, StringReader, StringWriter }
import java.nio.ByteBuffer
import java.nio.file.{ Files, FileVisitResult, Paths }
import java.nio.file.StandardOpenOption.*
import java.nio.file.StandardWatchEventKinds.*

import scala.util.Try

import Implicits.*
import FileVisitEvent.*
import TestPath.*

class PathTypeSpec extends org.scalatest.flatspec.AnyFlatSpec:
  given BufferSize = BufferSize(64)

  val text = "Now Peter Piper picked peppers but Run rocks rhymes."

  "File" should "be written to output stream and read from input stream" in {
    val bytes = text.getBytes("utf-8")
    val file = createTempFile()

    file.withOutputStream() { out => out.write(bytes) }

    file.withInputStream() { in =>
      val out = ByteArrayOutputStream() << in
      assert(out.toByteArray.corresponds(bytes)(_ == _))
    }

    assert(file.getBytes().corresponds(bytes)(_ == _))
  }

  it should "be written to writer and read from reader" in {
    val file = createTempFile()

    file.withWriter() { writer => writer.append(text) }

    file.withReader() { reader =>
      val writer = StringWriter() << reader
      assert(writer.toString == text)
    }

    assert(file.getText() == text)

    file.forEachLine { line =>
      assert(text.split("\n").contains(line))
    }
  }

  it should "be written to and read from channel" in {
    val file = createTempFile()
    val buffer = ByteBuffer.allocate(256)

    buffer.put(text.getBytes("utf-8")).flip()
    file.withChannel(WRITE) { channel => channel.write(buffer) }

    buffer.clear()
    file.withChannel(READ) { channel => channel.read(buffer) }
    buffer.flip()

    val chars = new Array[Byte](buffer.limit)
    buffer.get(chars)

    assert(String(chars, "UTF-8") == text)
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
    val p = Paths.get("/a/b/c")

    assert { p / "x" / "y" / "z" == Paths.get("/a/b/c/x/y/z") }

    assert { (p / "/x" / "/y" / "/z").normalize() == Paths.get("/a/b/c/x/y/z") }
    assert { (p / "../x" / "/y" / "/z").normalize() == Paths.get("/a/b/x/y/z") }
    assert { (p / "../x" / "../y" / "/z").normalize() == Paths.get("/a/b/y/z") }
    assert { (p / "../x" / "../y" / "../z").normalize() == Paths.get("/a/b/z") }
  }

  "PrintWriter" should "be created from file" in {
    val file = createTempFile()

    file.withPrintWriter()(writer => writer.print(text))

    file.withReader() { reader =>
      val writer = StringWriter() << reader
      assert(writer.toString == text)
    }

    assert(file.getText() == text)

    file.forEachLine { line =>
      assert(text.split("\n").contains(line))
    }
  }

  "Directory" should "be scanned" in {
    val dir = createTempDir()
    val subdir = createTempDir(dir)
    val file = createTempFile(dir)

    var count = 0
    dir.forEachFile { file =>
      val name = file.getFileName.toString
      assert(name.startsWith("little-io-"))
      count += 1
    }
    assert(count == 2)

    count = 0
    dir.forFiles("little-io-*.txt") { file =>
      val name = file.getFileName.toString
      assert(name.startsWith("little-io-"))
      assert(name.endsWith(".txt"))
      count += 1
    }
    assert(count == 1)
  }

  it should "be traversed" in {
    val dir = createTempDir()
    val subdir = createTempDir(dir)
    val file = createTempFile(subdir)

    var preDirCount = 0
    var postDirCount = 0
    var fileCount = 0

    dir.withVisitor {
      case PreVisitDirectory(path, _) =>
        preDirCount += 1
        assert(path == dir || path == subdir)
        FileVisitResult.CONTINUE

      case VisitFile(path, _) =>
        fileCount += 1
        assert(path == file)
        FileVisitResult.CONTINUE

      case PostVisitDirectory(path, _) =>
        postDirCount += 1
        assert(path == dir || path == subdir)
        FileVisitResult.CONTINUE
    }

    assert(preDirCount == 2)
    assert(postDirCount == 2)
    assert(fileCount == 1)
  }

  it should "not be traversed" in {
    val dir = createTempDir()
    val subdir = createTempDir(dir)
    val file1 = createTempFile(dir)
    val file2 = createTempFile(subdir)

    var preDirCount = 0
    var postDirCount = 0
    var fileCount = 0

    dir.withVisitor {
      case PreVisitDirectory(path, _) =>
        preDirCount += 1
        assert(path == dir || path == subdir)
        if path == subdir then
          FileVisitResult.SKIP_SUBTREE
        else FileVisitResult.CONTINUE

      case VisitFile(path, _) =>
        fileCount += 1
        assert(path == file1)
        FileVisitResult.CONTINUE

      case PostVisitDirectory(path, _) =>
        postDirCount += 1
        assert(path == dir)
        FileVisitResult.CONTINUE
    }

    assert(preDirCount == 2)
    assert(postDirCount == 1)
    assert(fileCount == 1)
  }

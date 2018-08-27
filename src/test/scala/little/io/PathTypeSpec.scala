package little.io

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream, StringReader, StringWriter }
import java.nio.file.{ Files, FileVisitResult }

import org.scalatest.FlatSpec

import Implicits._
import FileVisitEvent._
import TestPath._

class PathTypeSpec extends FlatSpec {
  implicit val bufferSize = BufferSize(64)

  val text = "Now Peter Piper picked peppers but Run rocks rhymes."

  s"Path" should "be written to output stream and read from input stream" in {
    val bytes = text.getBytes("utf-8")
    val file = createTempFile()
    
    file.withOutputStream() { out => out.write(bytes) }

    file.withInputStream() { in =>
      val out = new ByteArrayOutputStream() << in
      assert(out.toByteArray.corresponds(bytes)(_ == _))
    }

    assert(file.getBytes.corresponds(bytes)(_ == _))
  }

  it should "be written to writer and read from reader" in {
    val file = createTempFile()
    
    file.withWriter() { writer => writer.append(text) }

    file.withReader { reader =>
      val writer = new StringWriter() << reader
      assert(writer.toString == text)
    }

    assert(file.getText == text)

    var first = true

    file.forEachLine { line =>
      if (first) assert(line == text.split("\n").head)
      else assert(line == text.split("\n").last)

      first = false
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

  "File" should "be created and deleted" in {
    val file = createTempFile() << "a regular file"

    assert(file.exists())
    assert(!file.notExists())
    assert(file.isRegularFile())
    assert(!file.isDirectory())
    assert(!file.isSymbolicLink)
    assert(!file.isHidden)
    assert(file.isReadable)
    assert(file.isWritable)
    assert(!file.isExecutable)
    assert(Files.deleteIfExists(file))
    assert(!file.exists())
    assert(file.notExists())
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
    dir.forEachFile("little-io-*.txt") { file =>
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

    dir.walkFileTree {
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
}

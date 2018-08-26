package little.io

import java.io.{ ByteArrayOutputStream, StringWriter }
import java.nio.file.Files

import org.scalatest.FlatSpec

import Implicits._

class PathTypeSpec extends FlatSpec {
  val text = "Now Peter Piper picked peppers but Run rocks rhymes."

  s"Path" should "be written to output stream and read from input stream" in {
    val bytes = text.getBytes("utf-8")
    val file = Files.createTempFile("little-io-", ".txt")
    
    file.withOutputStream() { out => out.write(bytes) }

    file.withInputStream() { in =>
      val out = new ByteArrayOutputStream() << in
      assert(out.toByteArray.corresponds(bytes)(_ == _))
    }

    assert(file.getBytes.corresponds(bytes)(_ == _))
  }

  it should "be written to writer and read from reader" in {
    val file = Files.createTempFile("little-io-", ".txt")
    
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
    val file = Files.createTempFile("little-io-", ".txt")

    file.setText("abc")
    file.append("123".getBytes).append(".!?")

    assert(file.getText == "abc123.!?")

    file.setBytes("ABC".getBytes)
    file.append("123").append(".!?".getBytes)

    assert(file.getText == "ABC123.!?")
  }
}

package little.io

import java.io.{ ByteArrayOutputStream, File, StringWriter }

import org.scalatest.FlatSpec

import Implicits._

class FileTypeSpec extends FlatSpec {
  val text = "Now Peter Piper picked peppers but Run rocks rhymes."

  s"File" should "be written to output stream and read from input stream" in {
    val bytes = text.getBytes("utf-8")
    val file = File.createTempFile("little-io-", ".txt")
    
    file.withOutputStream(out => out.write(bytes))

    file.withInputStream { in =>
      val out = new ByteArrayOutputStream() << in
      assert(out.toByteArray.corresponds(bytes)(_ == _))
    }

    assert(file.getBytes.corresponds(bytes)(_ == _))
  }

  it should "be written to writer and read from reader" in {
    val file = File.createTempFile("little-io-", ".txt")
    
    file.withWriter(writer => writer.append(text))

    file.withReader { reader =>
      val writer = new StringWriter() << reader
      assert(writer.toString == text)
    }

    assert(file.getText == text)
  }
}

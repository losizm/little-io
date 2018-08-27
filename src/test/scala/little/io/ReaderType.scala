package little.io

import java.io.{ StringReader, StringWriter }

import org.scalatest.FlatSpec

import Implicits._

class ReaderTypeSpec extends FlatSpec {
  s"Reader" should "read characters into buffer" in {
    implicit val bufferSize = BufferSize(8)

    val text = "Now Peter Piper picked peppers but Run rocks rhymes."
    val in = new StringReader(text)
    val out = new StringWriter()

    in.forEachCharArray { (buf, len) => out.write(buf, 0, len) }

    assert(out.toString == text)
  }
}

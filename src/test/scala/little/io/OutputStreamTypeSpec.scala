package little.io

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

import org.scalatest.FlatSpec

import Implicits._

class OutputStreamTypeSpec extends FlatSpec {
  s"OutputStream" should "write bytes from InputStream" in {
    val text = "Now Peter Piper picked peppers but Run rocks rhymes."
    val in = new ByteArrayInputStream(text.getBytes)
    val out = new ByteArrayOutputStream() << in << text.getBytes
    assert(out.toString == (text * 2))
  }
}

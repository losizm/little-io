package little.io

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

import org.scalatest.FlatSpec

import Implicits._

class OutputStreamTypeSpec extends FlatSpec {
  s"OutputStream" should "write bytes from InputStream" in {
    val message = "Now Peter Piper picked peppers but Run rocks rhymes."
    val in = new ByteArrayInputStream(message.getBytes())
    val out = new ByteArrayOutputStream()
    out << in
    assert(out.toString() == message)
  }
}

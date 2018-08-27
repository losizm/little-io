package little.io

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

import org.scalatest.FlatSpec

import Implicits._

class InputStreamTypeSpec extends FlatSpec {
  s"InputStream" should "read bytes into buffer" in {
    implicit val bufferSize = BufferSize(8)

    val text = "Now Peter Piper picked peppers but Run rocks rhymes."
    val in = new ByteArrayInputStream(text.getBytes)
    val out = new ByteArrayOutputStream()

    in.forEachByteArray { (buf, len) => out.write(buf, 0, len) }

    assert(out.toString == text)
  }
}

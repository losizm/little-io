package little.io

import java.io.{ StringReader, StringWriter }

import org.scalatest.FlatSpec

import Implicits._

class WriterTypeSpec extends FlatSpec {
  s"Writer" should "write characters from Reader" in {
    val text = "Now Peter Piper picked peppers but Run rocks rhymes."
    val reader = new StringReader(text)
    val writer = new StringWriter() << reader
    assert(writer.toString == text)
  }
}


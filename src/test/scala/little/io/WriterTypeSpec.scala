package little.io

import java.io.{ StringReader, StringWriter }

import org.scalatest.FlatSpec

import Implicits._

class WriterTypeSpec extends FlatSpec {
  s"Writer" should "write characters from Reader" in {
    val message = "Now Peter Piper picked peppers but Run rocks rhymes."
    val reader = new StringReader(message)
    val writer = new StringWriter()
    writer << reader
    assert(writer.toString() == message)
  }
}


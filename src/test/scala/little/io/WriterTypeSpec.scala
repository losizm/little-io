package little.io

import java.io.{ StringReader, StringWriter }

import scala.compat.Platform.EOL

import org.scalatest.FlatSpec

import Implicits._

class WriterTypeSpec extends FlatSpec {
  s"Writer" should "write characters from Reader" in {
    val text = "Now Peter Piper picked peppers but Run rocks rhymes."
    val reader = new StringReader(text)
    val writer = new StringWriter() << reader << text
    assert(writer.toString == (text * 2))

    writer.writeLine(text)
    assert(writer.toString == (text * 3) + EOL)
  }
}


package little.io

import java.nio.file.Files

import scala.sys.{ props => sysProps }

import org.scalatest.FlatSpec

import Implicits.IoStringType

class IoStringTypeSpec extends FlatSpec {
  s"String" should "be converted to File" in {
    val file = sysProps("java.io.tmpdir").toFile
    assert(file.isDirectory)
  }

  it should "be converted to Path" in {
    val path = sysProps("java.io.tmpdir").toPath
    assert(Files.isDirectory(path))
  }
}

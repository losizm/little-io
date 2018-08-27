package little.io

import java.io.File
import java.nio.file.{ Files, Path }
import scala.util.Properties

object TestFile {
  def getTempDir(): File =
    new File(Properties.tmpDir)

  def createTempDir(dir: File = getTempDir()): File = {
    val file = Files.createTempDirectory(dir.toPath, "little-io-").toFile
    file.deleteOnExit()
    file
  }

  def createTempFile(dir: File = getTempDir()): File = {
    val file = Files.createTempFile(dir.toPath, "little-io-", ".txt").toFile
    file.deleteOnExit()
    file
  }
}

object TestPath {
  def getTempDir(): Path =
    TestFile.getTempDir().toPath

  def createTempDir(dir: Path = getTempDir()): Path =
    TestFile.createTempDir(dir.toFile).toPath

  def createTempFile(dir: Path = getTempDir()): Path =
    TestFile.createTempFile(dir.toFile).toPath
}

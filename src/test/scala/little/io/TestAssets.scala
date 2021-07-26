/*
 * Copyright 2021 Carlos Conyers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package little.io

import java.io.File
import java.nio.file.{ Files, Path }
import scala.util.Properties

object TestFile:
  def getTempDir(): File =
    File(Properties.tmpDir)

  def createTempDir(dir: File = getTempDir()): File =
    val file = Files.createTempDirectory(dir.toPath, "little-io-").toFile
    file.deleteOnExit()
    file

  def createTempFile(dir: File = getTempDir(), suffix: String = ".txt"): File =
    val file = Files.createTempFile(dir.toPath, "little-io-", suffix).toFile
    file.deleteOnExit()
    file

object TestPath:
  def getTempDir(): Path =
    TestFile.getTempDir().toPath

  def createTempDir(dir: Path = getTempDir()): Path =
    TestFile.createTempDir(dir.toFile).toPath

  def createTempFile(dir: Path = getTempDir()): Path =
    TestFile.createTempFile(dir.toFile).toPath

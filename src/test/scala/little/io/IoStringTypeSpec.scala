/*
 * Copyright 2019 Carlos Conyers
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

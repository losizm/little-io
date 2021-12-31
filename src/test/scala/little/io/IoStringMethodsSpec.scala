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

import java.nio.file.Files

import scala.sys.props as sysProps

class IoStringMethodsSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "convert String to File" in {
    val file = sysProps("java.io.tmpdir").toFile
    assert(file.isDirectory)
  }

  it should "convert String to Path" in {
    val path = sysProps("java.io.tmpdir").toPath
    assert(Files.isDirectory(path))
  }

  it should "convert String to and from URL-encoded String" in {
    val decoded = "https://github.com/losizm"
    val encoded = "https%3A%2F%2Fgithub.com%2Flosizm"

    assert(decoded.toUrlEncoded == encoded)
    assert(encoded.toUrlDecoded == decoded)
  }

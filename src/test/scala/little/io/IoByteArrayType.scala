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

import Implicits.IoByteArrayType

class IoByteArrayTypeSpec extends org.scalatest.flatspec.AnyFlatSpec:
  private val decoded = Array[Byte](87, 104, 121, 32, 100, 111, 101, 115, 32, 105, 116, 32, 109, 97, 116, 116, 101, 114, 63)
  private val encoded = Array[Byte](86, 50, 104, 53, 73, 71, 82, 118, 90, 88, 77, 103, 97, 88, 81, 103, 98, 87, 70, 48, 100, 71, 86, 121, 80, 119, 61, 61)

  it should "convert byte array to base64 encoded array" in {
    assert(decoded.toBase64Encoded.toSeq == encoded.toSeq)
  }

  it should "convert byte array to base64 decoded array" in {
    assert(encoded.toBase64Decoded.toSeq == decoded.toSeq)
  }

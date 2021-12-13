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

/** Provides extension methods to `Array[Byte]`. */
implicit class ByteArrayExt(bytes: Array[Byte]) extends AnyVal:
  /**
   * Converts bytes to base64 encoded array.
   *
   * @return newly-allocated array with encoded bytes
   */
  def toBase64Encoded: Array[Byte] =
    Base64Encoder.encode(bytes)

  /**
   * Converts bytes to base64 decoded array.
   *
   * @return newly-allocated array with decoded bytes
   */
  def toBase64Decoded: Array[Byte] =
    Base64Decoder.decode(bytes)

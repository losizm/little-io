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
import java.net.{ URLDecoder, URLEncoder }
import java.nio.file.{ Path, Paths }

/** Provides extension methods to `String`. */
implicit class StringExt(s: String) extends AnyVal:
  /** Converts string to File. */
  def toFile: File = File(s)

  /** Converts string to Path. */
  def toPath: Path = Paths.get(s)

  /**
   * Converts string to URL-encoded value using UTF-8 character encoding.
   *
   * @param charset character encoding
   */
  def toUrlEncoded: String =
    URLEncoder.encode(s, "UTF-8")

  /**
   * Converts string to URL-encoded value using specified character encoding.
   *
   * @param charset character encoding
   */
  def toUrlEncoded(charset: String): String =
    URLEncoder.encode(s, charset)

  /**
   * Converts string to URL-decoded value using UTF-8 character encoding.
   *
   * @param charset character encoding
   */
  def toUrlDecoded: String =
    URLDecoder.decode(s, "UTF-8")

  /**
   * Converts string to URL-decoded value using specified character encoding.
   *
   * @param charset character encoding
   */
  def toUrlDecoded(charset: String): String =
    URLDecoder.decode(s, charset)

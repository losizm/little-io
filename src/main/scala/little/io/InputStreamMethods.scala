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

import java.io.{ InputStream, ByteArrayOutputStream }

/**
 * Provides extension methods for `java.io.InputStream`.
 *
 * @see [[OutputStreamMethods]]
 */
implicit class InputStreamMethods[T <: InputStream](in: T) extends AnyVal:
  /** Gets remaining bytes. */
  def getBytes(): Array[Byte] =
    val out = ByteArrayOutputStream()
    val buf = new Array[Byte](bufferSize.value)
    var len = 0

    while { len = in.read(buf); len != -1 } do
      out.write(buf, 0, len)
    out.toByteArray

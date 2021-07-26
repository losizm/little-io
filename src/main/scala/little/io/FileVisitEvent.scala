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

import java.io.IOException
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

/**
 * Defines file visit events.
 *
 * @see [[Implicits.PathType.withVisitor PathType.withVisitor]]
 */
enum FileVisitEvent:
  /** Occurs before entries in directory are visited. */
  case PreVisitDirectory(directory: Path, attributes: BasicFileAttributes) extends FileVisitEvent

  /**
   * Occurs after entries in directory and all of their descendents are visited.
   */
  case PostVisitDirectory(directory: Path, exception: Option[IOException]) extends FileVisitEvent

  /** Occurs when file is visited. */
  case VisitFile(file: Path, attributes: BasicFileAttributes) extends FileVisitEvent

  /** Occurs when file could not be visited. */
  case VisitFileFailed(file: Path, exception: IOException) extends FileVisitEvent

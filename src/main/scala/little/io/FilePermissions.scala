/*
 * Copyright 2018 Carlos Conyers
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

import java.nio.file.attribute.PosixFilePermission
import java.util.{ Set => JavaSet }

import PosixFilePermission._

import scala.collection.JavaConverters.asScalaSet

/**
 * Represents single set of read/write/execute permissions.
 *
 * @see [[FilePermissions]]
 */
trait PermissionSet {
  /** Test whether read permission is set. */
  def read: Boolean

  /** Test whether write permission is set. */

  def write: Boolean

  /** Test whether write permission is set. */
  def execute: Boolean
}

private case class PermissionSetImpl(read: Boolean, write: Boolean, execute: Boolean) extends PermissionSet {
  override lazy val toString = (if (read) "r" else "-") + (if (read) "w" else "-") + (if (read) "x" else "-")
}

/**
 * Represents full set of owner/group/other file permissions.
 *
 * @see [[Implicits.PathType.getFilePermissions]]
 */
trait FilePermissions {
  /** Gets owner permission set. */
  def owner: PermissionSet

  /** Gets group permission set. */
  def group: PermissionSet

  /** Gets other permission set. */
  def other: PermissionSet

  /** Converts to set of {@code PosixFilePermission}s. */
  def toPosixFilePermissions: Set[PosixFilePermission]
}

private case class FilePermissionsImpl(perms: JavaSet[PosixFilePermission]) extends FilePermissions {
  lazy val owner = PermissionSetImpl(perms.contains(OWNER_READ), perms.contains(OWNER_WRITE), perms.contains(OWNER_EXECUTE))
  lazy val group = PermissionSetImpl(perms.contains(GROUP_READ), perms.contains(GROUP_WRITE), perms.contains(GROUP_EXECUTE))
  lazy val other = PermissionSetImpl(perms.contains(OTHERS_READ), perms.contains(OTHERS_WRITE), perms.contains(OTHERS_EXECUTE))
  lazy val toPosixFilePermissions = asScalaSet(perms).toSet
  override lazy val toString = owner.toString + group.toString + other.toString
}


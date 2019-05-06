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

import PosixFilePermission._

/**
 * Represents single set of read, write, and execute permissions.
 *
 * @see [[FilePermissions]]
 */
trait PermissionSet {
  /** Test whether read permission is set. */
  def readable: Boolean

  /** Test whether write permission is set. */
  def writable: Boolean

  /** Test whether execute permission is set. */
  def executable: Boolean
}

/** Provides factory methods for creating PermissionSet. */
object PermissionSet {
  /**
   * Creates PermissionSet with supplied settings.
   *
   * @param readable sets read permission
   * @param writable sets write permission
   * @param executable sets execute permission
   */
  def apply(readable: Boolean, writable: Boolean, executable: Boolean): PermissionSet =
    PermissionSetImpl(readable, writable, executable)

  /**
   * Creates PermissionSet from formatted string.
   *
   * The string must match the regular expression `[r-][w-][x-]{3}`; otherwise
   * `IllegalArgumentException` is thrown.
   *
   * @param perms permission set
   */
  def apply(perms: String): PermissionSet =
    perms.matches("[r-][w-][x-]") match {
      case true  =>  PermissionSetImpl(perms(0) == 'r', perms(1) == 'w', perms(2) == 'x')
      case false => throw new IllegalArgumentException("Invalid permission set")
    }
}

/**
 * Represents full set of owner, group, and other file permissions.
 *
 * @see [[Implicits.PathType.getFilePermissions PathType.getFilePermissions]]
 */
trait FilePermissions {
  /** Gets owner permission set. */
  def owner: PermissionSet

  /** Gets group permission set. */
  def group: PermissionSet

  /** Gets other permission set. */
  def other: PermissionSet

  /** Converts permissions to `PosixFilePermission`s. */
  def toPosixFilePermissions: java.util.Set[PosixFilePermission]
}

/** Provides factory methods for creating FilePermissions. */
object FilePermissions {
  /**
   * Creates FilePermissions with supplied permission sets.
   *
   * @param owner owner permissions
   * @param group group permissions
   * @param other other permissions
   */
  def apply(owner: PermissionSet, group: PermissionSet, other: PermissionSet): FilePermissions = {
    val perms = new java.util.HashSet[PosixFilePermission]

    if (owner.readable) perms.add(OWNER_READ)
    if (owner.writable) perms.add(OWNER_WRITE)
    if (owner.executable) perms.add(OWNER_EXECUTE)

    if (group.readable) perms.add(GROUP_READ)
    if (group.writable) perms.add(GROUP_WRITE)
    if (group.executable) perms.add(GROUP_EXECUTE)

    if (other.readable) perms.add(OTHERS_READ)
    if (other.writable) perms.add(OTHERS_WRITE)
    if (other.executable) perms.add(OTHERS_EXECUTE)

    FilePermissionsImpl(perms)
  }

  /**
   * Creates FilePermissions from formatted string.
   *
   * The string must match the regular expression `[r-][w-][x-]{3}`, where the 3
   * sets of permissions are owner, group, and other respectively; otherwise
   * `IllegalArgumentException` is thrown.
   *
   * @param perms file permissions
   */
  def apply(perms: String): FilePermissions =
    perms.matches("([r-][w-][x-]){3}") match {
      case true =>
        val toPosixFilePermissions = new java.util.HashSet[PosixFilePermission]

        if (perms(0) == 'r') toPosixFilePermissions.add(OWNER_READ)
        if (perms(1) == 'w') toPosixFilePermissions.add(OWNER_WRITE)
        if (perms(2) == 'x') toPosixFilePermissions.add(OWNER_EXECUTE)

        if (perms(3) == 'r') toPosixFilePermissions.add(GROUP_READ)
        if (perms(4) == 'w') toPosixFilePermissions.add(GROUP_WRITE)
        if (perms(5) == 'x') toPosixFilePermissions.add(GROUP_EXECUTE)

        if (perms(6) == 'r') toPosixFilePermissions.add(OTHERS_READ)
        if (perms(7) == 'w') toPosixFilePermissions.add(OTHERS_WRITE)
        if (perms(8) == 'x') toPosixFilePermissions.add(OTHERS_EXECUTE)

        FilePermissionsImpl(toPosixFilePermissions)

      case false => throw new IllegalArgumentException("Invalid file permissions")
    }
}

private case class PermissionSetImpl(readable: Boolean, writable: Boolean, executable: Boolean) extends PermissionSet {
  override lazy val toString = (if (readable) "r" else "-") + (if (writable) "w" else "-") + (if (executable) "x" else "-")
}

private case class FilePermissionsImpl(toPosixFilePermissions: java.util.Set[PosixFilePermission]) extends FilePermissions {
  lazy val owner = PermissionSetImpl(
    toPosixFilePermissions.contains(OWNER_READ),
    toPosixFilePermissions.contains(OWNER_WRITE),
    toPosixFilePermissions.contains(OWNER_EXECUTE)
  )

  lazy val group = PermissionSetImpl(
    toPosixFilePermissions.contains(GROUP_READ),
    toPosixFilePermissions.contains(GROUP_WRITE),
    toPosixFilePermissions.contains(GROUP_EXECUTE)
  )

  lazy val other = PermissionSetImpl(
    toPosixFilePermissions.contains(OTHERS_READ),
    toPosixFilePermissions.contains(OTHERS_WRITE),
    toPosixFilePermissions.contains(OTHERS_EXECUTE)
  )

  override lazy val toString = owner.toString + group.toString + other.toString
}

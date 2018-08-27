package little.io

import java.io.IOException
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

/**
 * Occurs when walking file tree.
 *
 * @see [[Implicits.PathType.walkFileTree]]
 */
sealed trait FileVisitEvent

/**
 * Contains file visit events.
 *
 * @see [[Implicits.PathType.walkFileTree]]
 */
object FileVisitEvent {
  /** Occurs before entries in directory are visited. */
  case class PreVisitDirectory(directory: Path, attributes: BasicFileAttributes) extends FileVisitEvent

  /**
   * Occurs after entries in directory, and all of their descendents, have
   * been visited.
   */
  case class PostVisitDirectory(directory: Path, exception: Option[IOException]) extends FileVisitEvent

  /** Occurs for file in directory. */
  case class VisitFile(file: Path, attributes: BasicFileAttributes) extends FileVisitEvent

  /** Occurs for file that could not be visited. */
  case class VisitFileFailed(file: Path, exception: IOException) extends FileVisitEvent
}

package little.io

import java.nio.file._
import java.util.concurrent.atomic.AtomicLong

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

private object WatchExecutionContext extends ExecutionContext {
  private val threadCount = new AtomicLong(0)

  def execute(runner: Runnable): Unit =
    new Thread(runner, s"WatchHandle-${threadCount.incrementAndGet}").start()

  def reportFailure(cause: Throwable): Unit = ()
}

/**
 * Provides opaque handle to watch service.
 *
 * A handle is obtained via [[Implicits.PathType.watch]], which registers path
 * to watch service.
 *
 * {{{
 * import java.nio.file.Paths
 * import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
 * import little.io.Implicits.PathType
 *
 * val dir = Paths.get(".")
 *
 * val handle = dir.watch(ENTRY_CREATE) { evt ⇒
 *   println(s"${evt.context} was created.")
 * }
 * }}}
 */
final class WatchHandle private[io] (service: WatchService, key: WatchKey, watcher: WatchEvent[_] => Unit) {
  private implicit val ec = WatchExecutionContext
  private var closed = false

  Future {
    while (!closed) {
      Try {
        val taken = service.take()
        taken.pollEvents().forEach(event => watcher(event))
        taken.reset()
      }
    }
  } onComplete {
    case result => if (!closed) close()
  }

  /** Closes underlying watch service. */
  def close(): Unit = {
    Try(key.cancel())
    Try(service.close())
    closed = true
  }

  /** Tests whether underlying watch service is closed. */
  def isClosed: Boolean = closed
}

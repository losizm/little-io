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
 * Provides handle to watch service and key.
 *
 * @see [[Implicits.PathType.watch]]
 */
final class WatchHandle private[io] (_service: WatchService, _key: WatchKey, watcher: WatchEvent[_] => Unit) {
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

  /** Gets watch service. */
  def service: WatchService = _service

  /** Gets watch key. */
  def key: WatchKey = _key

  /** Cancels watch key and closes watch service. */
  def close(): Unit = {
    Try(key.cancel())
    Try(service.close())
    closed = true
  }

  /** Tests whether handle is closed. */
  def isClosed: Boolean = closed
}

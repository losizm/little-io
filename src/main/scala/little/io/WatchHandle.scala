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

import java.nio.file.{ WatchEvent, WatchKey, WatchService }
import java.util.concurrent.atomic.AtomicLong

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

/**
 * Provides opaque handle to watcher.
 *
 * A handle is obtained via [[PathMethods.withWatcher PathMethods.withWatcher()]].
 *
 * {{{
 * import java.nio.file.Paths
 * import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
 * import little.io.PathMethods
 *
 * val dir = Paths.get(".")
 *
 * // Print message when file is created
 * val handle = dir.withWatcher(ENTRY_CREATE) { evt =>
 *   println(s"\${evt.context} was created.")
 * }
 *
 * Thread.sleep(60 * 1000)
 *
 * // Close handle when finished
 * handle.close()
 * }}}
 */
final class WatchHandle private[io] (service: WatchService, key: WatchKey, watcher: WatchEvent[_] => Unit):
  private given ExecutionContext = WatchExecutionContext

  @volatile
  private var closed = false

  Future {
    try
      while !closed do
        val taken = service.take()

        taken.pollEvents().forEach(watcher(_))

        if !taken.reset() then
          close()
    finally
      if !closed then
        close()
  }

  /** Closes underlying watcher. */
  def close(): Unit =
    closed = true
    Try(key.cancel())
    Try(service.close())

  /** Tests whether underlying watcher is closed. */
  def isClosed: Boolean =
    closed

private object WatchExecutionContext extends ExecutionContext:
  private val threadCount = AtomicLong(0)

  def execute(runner: Runnable): Unit =
    val thread = Thread(runner, s"little-io-WatchHandle-${threadCount.incrementAndGet}")
    thread.setDaemon(true)
    thread.start()

  def reportFailure(cause: Throwable): Unit = ()

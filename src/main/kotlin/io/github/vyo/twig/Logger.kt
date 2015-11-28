package io.github.vyo.twig

import nl.komponents.kovenant.Kovenant
import nl.komponents.kovenant.async
import nl.komponents.kovenant.disruptor.queue.disruptorWorkQueue
import kotlin.concurrent.currentThread

/**
 * Created by Manuel Weidmann on 24.11.2015.
 */

open class Logger(val caller: Any) {
    var threshold: Level

    init {
        threshold = root.threshold
    }

    companion object root : Logger("root") {
        init {
            threshold = Level.INFO
            Kovenant.context {
                callbackContext {
                    dispatcher {
                        concurrentTasks = Runtime.getRuntime().availableProcessors()
                        workQueue = disruptorWorkQueue()
                    }
                }
            }
            info("worker count: ${Runtime.getRuntime().availableProcessors()}")
            info("work queue size 1024")
        }
    }

    fun log(message: String, level: Level) {
        if (level < threshold) return
        val thread: Thread = currentThread
        async {
            println("{${escape("thread")}:${escape(thread)}" +
                    "${escape("time")}:${escape(System.currentTimeMillis())}," +
                    "${escape("level")}:${escape(level.toInt())}," +
                    "${escape("name")}:${escape(caller)}," +
                    "${escape("message")}:${escape(message)}}")
        } fail {
            println("{${escape("thread")}:${escape(thread)}" +
                    "${escape("time")}:${escape(System.currentTimeMillis())}," +
                    "${escape("level")}:${Level.FATAL.toInt()}}" +
                    "${escape("name")}:${escape(this)}," +
                    "${escape("message")}:${escape("logging failed: ${it.message}")}," +
                    "${escape("originalLevel")}:${escape(level.toInt())}," +
                    "${escape("originalName")}:${escape(caller)}," +
                    "${escape("originalMessage")}:${escape(message)}}")
        }
    }

    private fun escape(string: Any): String {
        return "\"${string.toString()}\""
    }

    fun trace(message: String) {
        log(message, Level.TRACE)
    }

    fun debug(message: String) {
        log(message, Level.DEBUG)
    }

    fun info(message: String) {
        log(message, Level.INFO)
    }

    fun warn(message: String) {
        log(message, Level.WARN)
    }

    fun error(message: String) {
        log(message, Level.ERROR)
    }

    fun fatal(message: String) {
        log(message, Level.FATAL)
    }
}
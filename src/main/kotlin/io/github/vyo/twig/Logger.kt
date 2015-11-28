package io.github.vyo.twig

import nl.komponents.kovenant.Kovenant
import nl.komponents.kovenant.async
import nl.komponents.kovenant.disruptor.queue.disruptorWorkQueue
import kotlin.concurrent.currentThread

/**
 * Created by Manuel Weidmann on 24.11.2015.
 */

open class Logger(val caller: Any) : LoggerInterface {
    override var threshold: Level = Logger.root.threshold

    companion object root : LoggerInterface {
        override var threshold: Level = Level.INFO
            set(value) {
                field = value
                info("log level $threshold")
            }
        private val logger: Logger = Logger("root")

        init {
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
            info("log level $threshold")
        }

        override fun log(message: String, level: Level) {
            logger.log(message, level)
        }

        override fun trace(message: String) {
            logger.trace(message)
        }

        override fun debug(message: String) {
            logger.debug(message)
        }

        override fun info(message: String) {
            logger.info(message)
        }

        override fun warn(message: String) {
            logger.warn(message)
        }

        override fun error(message: String) {
            logger.error(message)
        }

        override fun fatal(message: String) {
            logger.fatal(message)
        }
    }


    private fun escape(string: Any): String {
        return "\"${string.toString()}\""
    }

    override fun log(message: String, level: Level) {
        if (level < threshold) return
        val thread: Thread = currentThread
        async {
            println("{${escape("thread")}:${escape(thread)}," +
                    "${escape("time")}:${escape(System.currentTimeMillis())}," +
                    "${escape("level")}:${escape(level.toInt())}," +
                    "${escape("name")}:${escape(caller)}," +
                    "${escape("message")}:${escape(message)}}")
        } fail {
            println("{${escape("thread")}:${escape(thread)}," +
                    "${escape("time")}:${escape(System.currentTimeMillis())}," +
                    "${escape("level")}:${Level.FATAL.toInt()}}" +
                    "${escape("name")}:${escape(this)}," +
                    "${escape("message")}:${escape("logging failed: ${it.message}")}," +
                    "${escape("originalLevel")}:${escape(level.toInt())}," +
                    "${escape("originalName")}:${escape(caller)}," +
                    "${escape("originalMessage")}:${escape(message)}}")
        }
    }

    override fun trace(message: String) {
        log(message, Level.TRACE)
    }

    override fun debug(message: String) {
        log(message, Level.DEBUG)
    }

    override fun info(message: String) {
        log(message, Level.INFO)
    }

    override fun warn(message: String) {
        log(message, Level.WARN)
    }

    override fun error(message: String) {
        log(message, Level.ERROR)
    }

    override fun fatal(message: String) {
        log(message, Level.FATAL)
    }
}
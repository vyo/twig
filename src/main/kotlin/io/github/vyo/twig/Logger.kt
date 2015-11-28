package io.github.vyo.twig

import nl.komponents.kovenant.Kovenant
import nl.komponents.kovenant.async
import nl.komponents.kovenant.disruptor.queue.disruptorWorkQueue
import kotlin.concurrent.currentThread

/**
 * Created by Manuel Weidmann on 24.11.2015.
 */

open class Logger(val caller: Any, override var customFields: Array<String> = arrayOf()) : LoggerInterface {
    override var threshold: Level = root.threshold

    companion object root : LoggerInterface {
        override var threshold: Level = Level.INFO
            set(value) {
                field = value
                info("log level $threshold")
            }
        override val customFields: Array<String> = arrayOf()
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
            info("work queue size: 1024")
            info("log level: $threshold")
        }

        override fun log(level: Level, vararg message: String) {
            logger.log(level, message[0])
        }

        override fun trace(vararg message: String) {
            logger.trace(message[0])
        }

        override fun debug(vararg message: String) {
            logger.debug(message[0])
        }

        override fun info(vararg message: String) {
            logger.info(message[0])
        }

        override fun warn(vararg message: String) {
            logger.warn(message[0])
        }

        override fun error(vararg message: String) {
            logger.error(message[0])
        }

        override fun fatal(vararg message: String) {
            logger.fatal(message[0])
        }
    }


    private fun escape(string: Any): String {
        return "\"${string.toString()}\""
    }

    override fun log(level: Level, vararg message: String) {
        if (level < threshold) return
        if (message.size == 0) return
        val thread: Thread = currentThread
        async {
            var entry: String = "{${escape("thread")}:${escape(thread)}," +
                    "${escape("time")}:${escape(System.currentTimeMillis())}," +
                    "${escape("level")}:${escape(level.toInt())}," +
                    "${escape("name")}:${escape(caller)}," +
                    "${escape("message")}:${escape(message[0])}"

            for (index in 2..message.size) {
                println(",${escape(customFields[index - 2])}:${escape(message[index - 1])}")
                entry += ",${escape(customFields[index - 2])}:${escape(message[index - 1])}"
            }

            entry += "}"
            println(entry)
        } fail {
            var entry: String = "{${escape("thread")}:${escape(thread)}," +
                    "${escape("time")}:${escape(System.currentTimeMillis())}," +
                    "${escape("level")}:${io.github.vyo.twig.Level.FATAL.toInt()}}" +
                    "${escape("name")}:${escape(this)}," +
                    "${escape("message")}:${escape("logging failed: ${it.message}")}," +
                    "${escape("originalLevel")}:${escape(level.toInt())}," +
                    "${escape("originalName")}:${escape(caller)}," +
                    "${escape("originalMessage")}:${escape(message[0])}"

            for (index in 2..message.size) {
                println(",${escape(customFields[index - 2])}:${escape(message[index - 1])}")
                entry += ",${escape(customFields[index - 2])}:${escape(message[index - 1])}"
            }

            entry += "}"
            println(entry)
        }
    }

    override fun trace(vararg message: String) {
        log(Level.TRACE, *message)
    }

    override fun debug(vararg message: String) {
        log(Level.DEBUG, *message)
    }

    override fun info(vararg message: String) {
        log(Level.INFO, *message)
    }

    override fun warn(vararg message: String) {
        log(Level.WARN, *message)
    }

    override fun error(vararg message: String) {
        log(Level.ERROR, *message)
    }

    override fun fatal(vararg message: String) {
        log(Level.FATAL, *message)
    }
}
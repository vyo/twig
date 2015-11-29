package io.github.vyo.twig.logger

import io.github.vyo.twig.appender.Appender
import io.github.vyo.twig.appender.ConsoleAppender
import nl.komponents.kovenant.Kovenant
import nl.komponents.kovenant.async
import nl.komponents.kovenant.disruptor.queue.disruptorWorkQueue
import java.lang.management.ManagementFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.currentThread

/**
 * Created by Manuel Weidmann on 24.11.2015.
 */

open class Logger(val caller: Any,
                  override var appender: Appender = Logger.root.appender,
                  override var customFields: Array<String> = arrayOf()) : LoggerInterface {
    override var threshold: Level = root.threshold

    companion object root : LoggerInterface {
        override var appender: Appender = ConsoleAppender()
            set(value) {
                field = value
                logger.appender = value
                info("appender $appender")
            }
        override var threshold: Level = Level.INFO
            set(value) {
                field = value
                info("log level $threshold")
            }
        override val customFields: Array<String> = arrayOf()
        val processInfo: String = ManagementFactory.getRuntimeMXBean().name
        val pid: Int = Integer.parseInt(processInfo.split('@')[0])
        val hostName: String = processInfo.split('@')[1]
        val timeZone: TimeZone = TimeZone.getTimeZone("UTC");
        val isoFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        init {
            isoFormat.timeZone = timeZone
        }

        private val logger: Logger = Logger("TwigRootLogger")

        init {
            Kovenant.context {
                callbackContext {
                    dispatcher {
                        concurrentTasks = Runtime.getRuntime().availableProcessors()
                        workQueue = disruptorWorkQueue()
                    }
                }
            }
            info("logging worker count: ${Runtime.getRuntime().availableProcessors()}")
            info("logging work queue size: 1024")
            info("root log level: $threshold")
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
            var entry: String = "{${escape("hostname")}:${escape(hostName)}," +
                    "${escape("pid")}:$pid," +
                    "${escape("thread")}:${escape(thread)}," +
                    "${escape("time")}:${escape(isoFormat.format(Date(System.currentTimeMillis())))}," +
                    "${escape("level")}:${level.toInt()}," +
                    "${escape("name")}:${escape(caller)}," +
                    "${escape("msg")}:${escape(message[0])}"

            for (index in 2..message.size) {
                entry += ",${escape(customFields[index - 2])}:${escape(message[index - 1])}"
            }

            entry += ",${escape("v")}:0}"
            appender.write(entry)
        } fail {
            var entry: String = "{${escape("hostname")}:${escape(hostName)}," +
                    "${escape("pid")}:$pid," +
                    "${escape("thread")}:${escape(thread)}," +
                    "${escape("time")}:${escape(isoFormat.format(Date(System.currentTimeMillis())))}," +
                    "${escape("level")}:${Level.FATAL.toInt()}" +
                    "${escape("name")}:${escape(this)}," +
                    "${escape("msg")}:${escape("logging failed: ${it.message}")}," +
                    "${escape("originalLevel")}:${escape(level.toInt())}," +
                    "${escape("originalName")}:${escape(caller)}," +
                    "${escape("originalMessage")}:${escape(message[0])}"

            for (index in 2..message.size) {
                entry += ",${escape(customFields[index - 2])}:${escape(message[index - 1])}"
            }

            entry += ",${escape("v")}:0}"
            appender.write(entry)
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
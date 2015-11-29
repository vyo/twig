package io.github.vyo.twig.logger

import io.github.vyo.twig.appender.Appender
import io.github.vyo.twig.appender.ConsoleAppender
import nl.komponents.kovenant.Kovenant
import nl.komponents.kovenant.Promise
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
                  override var appender: Appender = Logger.root.appender) : LoggerInterface {
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

        override fun log(level: Level, message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
            return logger.log(level, message)
        }

        override fun trace(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
            return logger.trace(message)
        }

        override fun debug(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
            return logger.debug(message)
        }

        override fun info(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
            return logger.info(message)
        }

        override fun warn(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
            return logger.warn(message)
        }

        override fun error(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
            return logger.error(message)
        }

        override fun fatal(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
            return logger.fatal(message)
        }
    }


    private fun escape(any: Any): String {
        return when (any) {
            is Array<*> -> "${any.toString()}"
            is Boolean -> "${any.toString()}"
            is Double -> "${any.toString()}"
            is Float -> "${any.toString()}"
            is Long -> "${any.toString()}"
            is Int -> "${any.toString()}"
            is Short -> "${any.toString()}"
            is Byte -> "${any.toString()}"
            is Char -> "${any.toString()}"
            else -> "\"${any.toString()}\""
        }
    }

    override fun log(level: Level, message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        if (level < threshold) return async { }
        if (customMessages.size == 0) return async { }
        val thread: Thread = currentThread
        return async {
            var entry: String = "{${escape("hostname")}:${escape(hostName)}," +
                    "${escape("pid")}:${escape(pid)}," +
                    "${escape("thread")}:${escape(thread)}," +
                    "${escape("time")}:${escape(isoFormat.format(Date(System.currentTimeMillis())))}," +
                    "${escape("level")}:${escape(level.toInt())}," +
                    "${escape("name")}:${escape(caller)}," +
                    "${escape("msg")}:${escape(message)}"

            for (customMessage in customMessages) {
                entry += ",${escape(customMessage.first)}:${escape(customMessage.second)}"
            }

            entry += ",${escape("v")}:${escape(0)}}"
            appender.write(entry)
        } fail {
            var entry: String = "{${escape("hostname")}:${escape(hostName)}," +
                    "${escape("pid")}:${escape(pid)}," +
                    "${escape("thread")}:${escape(thread)}," +
                    "${escape("time")}:${escape(isoFormat.format(Date(System.currentTimeMillis())))}," +
                    "${escape("level")}:${Level.FATAL.toInt()}" +
                    "${escape("name")}:${escape(this)}," +
                    "${escape("msg")}:${escape("logging failed: ${it.message}")}," +
                    "${escape("original level")}:${escape(level.toInt())}," +
                    "${escape("original name")}:${escape(caller)}," +
                    "${escape("original message")}:${escape(message)}"

            for (customMessage in customMessages) {
                entry += ",${"original " + escape(customMessage.first)}:${escape(customMessage.second)}"
            }

            entry += ",${escape("v")}:${escape(0)}"
            System.err.println(entry)
        }
    }


    override fun trace(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        return log(Level.TRACE, message, *customMessages)
    }

    override fun debug(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        return log(Level.DEBUG, message, *customMessages)
    }

    override fun info(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        return log(Level.INFO, message, *customMessages)
    }

    override fun warn(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        return log(Level.WARN, message, *customMessages)
    }

    override fun error(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        return log(Level.ERROR, message, *customMessages)
    }

    override fun fatal(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        return log(Level.FATAL, message, *customMessages)
    }
}
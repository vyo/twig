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
 * Default logger implementation for Twig
 *
 * Created by Manuel Weidmann on 24.11.2015.
 */

open class Logger(val caller: Any,
                  override var appender: Appender = Logger.root.appender) : LoggerInterface {
    override var level: Level = root.level

    companion object root {

        var appender: Appender = ConsoleAppender()
            set(value) {
                field = value
                logger.appender = value
                logger.info("appender $appender")
            }

        var level: Level = Level.INFO
            set(value) {
                field = value
                logger.info("log level $level")
            }

        private val processInfo: String = ManagementFactory.getRuntimeMXBean().name
        private val pid: Int = Integer.parseInt(processInfo.split('@')[0])
        private val hostName: String = processInfo.split('@')[1]
        private val timeZone: TimeZone = TimeZone.getTimeZone("UTC");
        private val isoFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private val logger: Logger = Logger("TwigRootLogger")

        init {
            isoFormat.timeZone = timeZone

            Kovenant.context {
                callbackContext {
                    dispatcher {
                        concurrentTasks = Runtime.getRuntime().availableProcessors()
                        workQueue = disruptorWorkQueue()
                    }
                }
            }

            logger.info("logging worker count: ${Runtime.getRuntime().availableProcessors()}")
            logger.info("logging work queue size: 1024")
            logger.info("root log level: $level")
        }
    }


    private fun escape(any: Any): String {
        return when (any) {
            is Array<*>,
            is Boolean,
            is Double,
            is Float,
            is Long,
            is Int,
            is Short,
            is Byte,
            is Char -> "${any.toString()}"
            else -> "\"${any.toString()}\""
        }
    }

    override fun log(level: Level, message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        if (level < level) return async { }
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
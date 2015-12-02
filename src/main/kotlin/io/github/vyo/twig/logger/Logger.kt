package io.github.vyo.twig.logger

import io.github.vyo.twig.appender.Appender
import io.github.vyo.twig.appender.ConsoleAppender
import nl.komponents.kovenant.Kovenant
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.async
import nl.komponents.kovenant.disruptor.queue.disruptorWorkQueue
import java.io.PrintWriter
import java.io.StringWriter
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

open class Logger @JvmOverloads constructor(val caller: Any,
                                            var appender: Appender = Logger.global.appender) {
    var level: Level = global.level

    companion object global {

        private final val TWIG_LEVEL: String = "TWIG_LEVEL"
        private final val TWIG_WORKERS: String = "TWIG_WORKERS"
        private final val TWIG_QUEUE: String = "TWIG_QUEUE"

        var appender: Appender = ConsoleAppender()
            set(value) {
                field = value
                logger.appender = value
                logger.info("global appender $appender")
            }

        var level: Level = Level.INFO
            set(value) {
                field = value
                logger.info("global log level $level")
            }

        private val processInfo: String = ManagementFactory.getRuntimeMXBean().name
        private val pid: Int = Integer.parseInt(processInfo.split('@')[0])
        private val hostName: String = processInfo.split('@')[1]
        private val timeZone: TimeZone = TimeZone.getTimeZone("UTC");
        private val isoFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private val logger: Logger = Logger("twig")

        init {
            isoFormat.timeZone = timeZone

            val queue: Int
            val workers: Int

            val levelEnv: String? = System.getenv(TWIG_LEVEL)
            try {
                if (levelEnv is String) {
                    level = Level.valueOf(levelEnv)
                }
            } catch (exception: IllegalArgumentException) {
                level = Level.INFO
            }

            val queueEnv: String? = System.getenv(TWIG_QUEUE)
            if (queueEnv is String && Integer.parseInt(queueEnv) is Int) {
                queue = Integer.parseInt(queueEnv)
            } else {
                queue = 1024
            }

            val workerEnv: String? = System.getenv(TWIG_WORKERS)
            if (workerEnv is String && Integer.parseInt(workerEnv) is Int) {
                workers = Integer.parseInt(workerEnv)
            } else {
                workers = Runtime.getRuntime().availableProcessors()
            }

            Kovenant.context {
                callbackContext {
                    dispatcher {
                        concurrentTasks = workers
                        workQueue = disruptorWorkQueue(capacity = queue)
                    }
                }
            }

            logger.info("logging worker count: $workers")
            logger.info("logging work queue size: $queue")
            if (level == Level.INFO) logger.info("global log level: $level")
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
            else -> "\"${escapeSpecialChars(any.toString())}\""
        }
    }

    private fun escapeSpecialChars(string: String): String {

        var escapedString: String = string
        escapedString = escapedString.replace("\\", "\\\\")
        escapedString = escapedString.replace("\n", "\\n")
        escapedString = escapedString.replace("\r", "\\r")
        escapedString = escapedString.replace("\b", "\\b")
        escapedString = escapedString.replace("\t", "\\t")
        escapedString = escapedString.replace("\"", "\\\"")

        return escapedString
    }

    fun log(level: Level, message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        if (level < level) return async { }
        val thread: Thread = currentThread
        val time: String = isoFormat.format(Date(System.currentTimeMillis()))
        return async {
            var entry: String = "{${escape("hostname")}:${escape(hostName)}," +
                    "${escape("pid")}:${escape(pid)}," +
                    "${escape("thread")}:${escape(thread)}," +
                    "${escape("time")}:${escape(time)}," +
                    "${escape("level")}:${escape(level.toInt())}," +
                    "${escape("name")}:${escape(caller)}," +
                    "${escape("msg")}:${escape(message)}"

            for (customMessage in customMessages) {
                entry += ",${escape(customMessage.first)}:${escape(customMessage.second)}"
            }

            entry += ",${escape("v")}:${escape(0)}}"
            appender.write(entry)
        } fail {
            //get the stacktrace
            var writer: StringWriter = StringWriter()
            it.printStackTrace(PrintWriter(writer))
            var stacktrace = writer.toString().replace("\n", "\\n")

            var entry: String = "{${escape("hostname")}:${escape(hostName)}," +
                    "${escape("pid")}:${escape(pid)}," +
                    "${escape("thread")}:${escape(thread)}," +
                    "${escape("time")}:${escape(isoFormat.format(Date(System.currentTimeMillis())))}," +
                    "${escape("level")}:${Level.FATAL.toInt()}" +
                    "${escape("name")}:${escape(this)}," +
                    "${escape("msg")}:${escape("logging exception: ${it.javaClass}")}," +
                    "${escape("exception message")}:${escape("${it.message}")}," +
                    "${escape("exception stacktrace")}:${escape(stacktrace)}," +
                    "${escape("original time")}:${escape(time)}," +
                    "${escape("original level")}:${escape(level.toInt())}," +
                    "${escape("original name")}:${escape(caller)}," +
                    "${escape("original msg")}:${escape(message)}"

            for (customMessage in customMessages) {
                entry += ",${escape("original " + customMessage.first)}:${escape(customMessage.second)}"
            }

            entry += ",${escape("v")}:${escape(0)}}"
            System.err.println(entry)
        }
    }


    fun trace(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        return log(Level.TRACE, message, *customMessages)
    }

    fun debug(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        return log(Level.DEBUG, message, *customMessages)
    }

    fun info(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        return log(Level.INFO, message, *customMessages)
    }

    fun warn(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        return log(Level.WARN, message, *customMessages)
    }

    fun error(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        return log(Level.ERROR, message, *customMessages)
    }

    fun fatal(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        return log(Level.FATAL, message, *customMessages)
    }
}
package io.github.vyo.twig.logger

import io.github.vyo.twig.appender.Appender
import io.github.vyo.twig.appender.ConsoleAppender
import nl.komponents.kovenant.Kovenant
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.task
import nl.komponents.kovenant.disruptor.queue.disruptorWorkQueue
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.management.ManagementFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Default logger implementation for Twig
 *
 * Created by Manuel Weidmann on 24.11.2015.
 */

open class Logger @JvmOverloads constructor(val caller: Any,
                                            var appender: Appender = Logger.global.appender,
                                            var level: Level = Logger.global.level,
                                            var serialiser: (any: Any) -> String = Logger.global.simpleSerialiser) {

    companion object global {

        private val TWIG_LEVEL: String = "TWIG_LEVEL"
        private val TWIG_WORKERS: String = "TWIG_WORKERS"
        private val TWIG_QUEUE: String = "TWIG_QUEUE"

        val simpleSerialiser = { any: Any ->

            when (any) {
            //TODO: handle complex arrays
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

        var serialiser: (any: Any) -> String = simpleSerialiser
            set(value) {
                field = value
                logger.info("global serialiser $serialiser")
            }

        private val processInfo: String = ManagementFactory.getRuntimeMXBean().name
        private val pid: Int = Integer.parseInt(processInfo.split('@')[0])
        private val hostName: String = processInfo.split('@')[1]
        private val timeZone: TimeZone = TimeZone.getTimeZone("UTC");
        private val isoFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private val logger: Logger = Logger("twig")

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
            logger.info("global log level: $level")
            logger.info("logging serialiser: $serialiser")
        }
    }

    private fun stringify(any: Any): String {
        return serialiser(any)
    }

    fun log(level: Level, message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        if (level < this.level) return task { }
        val thread: Thread = java.lang.Thread.currentThread()
        val time: String = isoFormat.format(Date(System.currentTimeMillis()))
        return task {
            var entry: String = "{${stringify("hostname")}:${stringify(hostName)}," +
                    "${stringify("pid")}:${stringify(pid)}," +
                    "${stringify("thread")}:${stringify(thread)}," +
                    "${stringify("time")}:${stringify(time)}," +
                    "${stringify("level")}:${stringify(level.toInt())}," +
                    "${stringify("name")}:${stringify(caller)}," +
                    "${stringify("msg")}:${stringify(message)}"

            for (customMessage in customMessages) {
                entry += ",${stringify(customMessage.first)}:${stringify(customMessage.second)}"
            }

            entry += ",${stringify("v")}:${stringify(0)}}"
            appender.write(entry)
        } fail {
            //get the stacktrace
            var writer: StringWriter = StringWriter()
            it.printStackTrace(PrintWriter(writer))
            var stacktrace = stringify(writer.toString())

            var entry: String = "{${stringify("hostname")}:${stringify(hostName)}," +
                    "${stringify("pid")}:${stringify(pid)}," +
                    "${stringify("thread")}:${stringify(thread)}," +
                    "${stringify("time")}:${stringify(isoFormat.format(Date(System.currentTimeMillis())))}," +
                    "${stringify("level")}:${stringify(Level.FATAL.toInt())}" +
                    "${stringify("name")}:${stringify(this)}," +
                    "${stringify("msg")}:${stringify("logging exception: ${it.javaClass}")}," +
                    "${stringify("exception message")}:${stringify("${it.message}")}," +
                    "${stringify("exception stacktrace")}:${stringify(stacktrace)}," +
                    "${stringify("original time")}:${stringify(time)}," +
                    "${stringify("original level")}:${stringify(level.toInt())}," +
                    "${stringify("original name")}:${stringify(caller)}," +
                    "${stringify("original msg")}:${stringify(message)}"

            for (customMessage in customMessages) {
                entry += ",${stringify("original " + customMessage.first)}:${stringify(customMessage.second)}"
            }

            entry += ",${stringify("v")}:${stringify(0)}}"
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

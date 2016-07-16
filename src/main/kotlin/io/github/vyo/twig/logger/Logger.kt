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

        private val TWIG_LEVEL = "TWIG_LEVEL"
        private val TWIG_WORKERS = "TWIG_WORKERS"
        private val TWIG_QUEUE = "TWIG_QUEUE"
        private val TWIG_EXPANSION_LEVEL = "TWIG_EXPANSION_LEVEL"
        private val TWIG_EXPANSION_DEPTH = "TWIG_EXPANSION_DEPTH"

        val simpleSerialiser = { any: Any ->

            when (any) {
                is Boolean,
                is Double,
                is Float,
                is Long,
                is Int,
                is Short,
                is Byte,
                is Char -> simplePrimitiveSerialiser(any)
                is Array<*> -> simpleArraySerialiser(any)
                is Collection<*> -> simpleCollectionSerialiser(any)
                else -> "\"${escapeSpecialChars(any.toString())}\""
            }

        }

        private fun simplePrimitiveSerialiser(any: Any): String {
            return "${any.toString()}"
        }

        private fun simpleArraySerialiser(array: Array<*>): String {
            var string = "["

            for (element: Any? in array) {
                if (element is Any) {
                    string += simpleSerialiser(element)
                    string += ","
                }
            }

            string += "]"

            return string.replace(",]", "]")
        }

        private fun simpleCollectionSerialiser(collection: Collection<*>): String {
            var string = "["

            for (element: Any? in collection) {
                if (element is Any) {
                    string += simpleSerialiser(element)
                    string += ","
                }
            }

            string += "]"

            return string.replace(",]", "]")
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

        var expansionLevel = Level.DEBUG
            set(value) {
                field = value
                logger.info("global throwable expansion level $expansionLevel")
            }

        var expansionDepth = 50
            set(value) {
                field = value
                logger.info("global throwable expansion depth $expansionDepth")
            }

        private val processInfo: String = ManagementFactory.getRuntimeMXBean().name
        private val pid: Int = Integer.parseInt(processInfo.split('@')[0])
        private val hostName: String = processInfo.split('@')[1]
        private val timeZone: TimeZone = TimeZone.getTimeZone("UTC")
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

            val expansionLevelEnv: String? = System.getenv(TWIG_EXPANSION_LEVEL)
            try {
                if (expansionLevelEnv is String) {
                    expansionLevel = Level.valueOf(expansionLevelEnv)
                }
            } catch (exception: IllegalArgumentException) {
                level = Level.DEBUG
            }

            val expansionDepthEnv: String? = System.getenv(TWIG_EXPANSION_DEPTH)
            if (expansionDepthEnv is String && Integer.parseInt(expansionDepthEnv) is Int) {
                expansionDepth = Integer.parseInt(expansionDepthEnv)
            } else {
                expansionDepth = 50
            }

            logger.info("logging worker count: $workers")
            logger.info("logging work queue size: $queue")
            logger.info("global log level: $level")
            logger.info("throwable expansion level: $expansionLevel")
            logger.info("throwable expansion depth: $expansionDepth")
        }
    }

    fun log(level: Level, message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception> {
        if (level < this.level) return task { }
        val thread: Thread = java.lang.Thread.currentThread()
        val time: String = isoFormat.format(Date(System.currentTimeMillis()))

        if (expansionLevel >= level && message is Throwable) {
            val stacktraceSize = Math.min(message.stackTrace.size, expansionDepth)

            val stacktrace = Arrays.copyOf(message.stackTrace, stacktraceSize)
            val adjustedCustomMessages = Arrays.copyOf(customMessages, customMessages.size + 1)

            adjustedCustomMessages[customMessages.size] = Pair("stacktrace", stacktrace)

            return log(level, "$message", *adjustedCustomMessages)
        }

        return task {
            var entry: String = "{${serialiser("hostname")}:${serialiser(hostName)}," +
                    "${serialiser("pid")}:${serialiser(pid)}," +
                    "${serialiser("thread")}:${serialiser(thread)}," +
                    "${serialiser("time")}:${serialiser(time)}," +
                    "${serialiser("level")}:${serialiser(level.toInt())}," +
                    "${serialiser("name")}:${serialiser(caller)}," +
                    "${serialiser("msg")}:${serialiser(message)}"

            for (customMessage in customMessages) {
                entry += ",${serialiser(customMessage.first)}:${serialiser(customMessage.second)}"
            }

            entry += ",${serialiser("v")}:${serialiser(0)}}"
            appender.write(entry)
        } fail {
            //get the stacktrace
            val writer: StringWriter = StringWriter()
            it.printStackTrace(PrintWriter(writer))
            val stacktrace = serialiser(writer.toString())

            var entry: String = "{${serialiser("hostname")}:${serialiser(hostName)}," +
                    "${serialiser("pid")}:${serialiser(pid)}," +
                    "${serialiser("thread")}:${serialiser(thread)}," +
                    "${serialiser("time")}:${serialiser(isoFormat.format(Date(System.currentTimeMillis())))}," +
                    "${serialiser("level")}:${serialiser(Level.FATAL.toInt())}" +
                    "${serialiser("name")}:${serialiser(this)}," +
                    "${serialiser("msg")}:${serialiser("logging exception: ${it.javaClass}")}," +
                    "${serialiser("exception message")}:${serialiser("${it.message}")}," +
                    "${serialiser("exception stacktrace")}:${serialiser(stacktrace)}," +
                    "${serialiser("original time")}:${serialiser(time)}," +
                    "${serialiser("original level")}:${serialiser(level.toInt())}," +
                    "${serialiser("original name")}:${serialiser(caller)}," +
                    "${serialiser("original msg")}:${serialiser(message)}"

            for (customMessage in customMessages) {
                entry += ",${serialiser("original " + customMessage.first)}:${serialiser(customMessage.second)}"
            }

            entry += ",${serialiser("v")}:${serialiser(0)}}"
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

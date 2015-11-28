package io.github.vyo.twig.appender

/**
 * Created by Manuel Weidmann on 28.11.2015.
 */
class ConsoleAppender : Appender {
    override fun write(logEntry: String) {
        println (logEntry)
    }
}
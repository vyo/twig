package io.github.vyo.twig.appender

/**
 * Created by Manuel Weidmann on 28.11.2015.
 */
interface Appender {
    fun write(logEntry: String)
}
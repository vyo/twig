package io.github.vyo.twig.logger

import io.github.vyo.twig.appender.Appender

/**
 * Created by Manuel Weidmann on 28.11.2015.
 */
interface LoggerInterface {

    var appender: Appender
    var threshold: Level
    val customFields: Array<String>

    fun log(level: Level, message: String, vararg customMessages: String)

    fun trace(message: String, vararg customMessages: String)

    fun debug(message: String, vararg customMessages: String)

    fun info(message: String, vararg customMessages: String)

    fun warn(message: String, vararg customMessages: String)

    fun error(message: String, vararg customMessages: String)

    fun fatal(message: String, vararg customMessages: String)
}
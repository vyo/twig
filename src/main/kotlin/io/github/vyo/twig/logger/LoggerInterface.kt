package io.github.vyo.twig.logger

import io.github.vyo.twig.appender.Appender

/**
 * Created by Manuel Weidmann on 28.11.2015.
 */
interface LoggerInterface {

    var appender: Appender
    var threshold: Level
    
    fun log(level: Level, message: Any, vararg customMessages: Pair<String, Any>)

    fun trace(message: Any, vararg customMessages: Pair<String, Any>)

    fun debug(message: Any, vararg customMessages: Pair<String, Any>)

    fun info(message: Any, vararg customMessages: Pair<String, Any>)

    fun warn(message: Any, vararg customMessages: Pair<String, Any>)

    fun error(message: Any, vararg customMessages: Pair<String, Any>)

    fun fatal(message: Any, vararg customMessages: Pair<String, Any>)
}
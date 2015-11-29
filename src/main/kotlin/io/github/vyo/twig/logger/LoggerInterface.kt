package io.github.vyo.twig.logger

import io.github.vyo.twig.appender.Appender
import nl.komponents.kovenant.Promise

/**
 * Created by Manuel Weidmann on 28.11.2015.
 */
interface LoggerInterface {

    var appender: Appender
    var threshold: Level
    
    fun log(level: Level, message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception>

    fun trace(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception>

    fun debug(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception>

    fun info(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception>

    fun warn(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception>

    fun error(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception>

    fun fatal(message: Any, vararg customMessages: Pair<String, Any>): Promise<Unit, Exception>
}
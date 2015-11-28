package io.github.vyo.twig.logger

/**
 * Created by Manuel Weidmann on 28.11.2015.
 */
interface LoggerInterface {

    var threshold: Level
    val customFields: Array<String>

    fun log(level: Level, vararg message: String)

    fun trace(vararg message: String)

    fun debug(vararg message: String)

    fun info(vararg message: String)

    fun warn(vararg message: String)

    fun error(vararg message: String)

    fun fatal(vararg message: String)
}
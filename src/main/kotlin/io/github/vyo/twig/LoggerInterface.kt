package io.github.vyo.twig

/**
 * Created by Manuel Weidmann on 28.11.2015.
 */
interface LoggerInterface {

    var threshold: Level

    fun log(message: String, level: Level)

    fun trace(message: String)

    fun debug(message: String)

    fun info(message: String)

    fun warn(message: String)

    fun error(message: String)

    fun fatal(message: String)
}
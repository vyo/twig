package io.github.vyo.twig.logger

/**
 * Created by Manuel Weidmann on 24.11.2015.
 *
 * Use bunyan log level convention
 */

enum class Level {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL
}

fun Level.toInt(): Int {
    when (this) {
        Level.TRACE -> return 10
        Level.DEBUG -> return 20
        Level.INFO -> return 30
        Level.WARN -> return 40
        Level.ERROR -> return 50
        Level.FATAL -> return 60
    }
}
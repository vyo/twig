package org.slf4j.impl

import io.github.vyo.twig.logger.SLF4JFactory
import org.slf4j.ILoggerFactory
import org.slf4j.spi.LoggerFactoryBinder

/**
 * Created by Manuel Weidmann on 18.02.16.
 */
object StaticLoggerBinder : LoggerFactoryBinder {

    @JvmStatic
    var REQUESTED_API_VERSION: String = "1.7.16"

    private val factory: SLF4JFactory = SLF4JFactory()

    @JvmStatic
    fun getSingleton(): StaticLoggerBinder {
        return StaticLoggerBinder
    }

    override fun getLoggerFactory(): ILoggerFactory? {
        return factory
    }

    override fun getLoggerFactoryClassStr(): String? {
        return factory.javaClass.name
    }
}
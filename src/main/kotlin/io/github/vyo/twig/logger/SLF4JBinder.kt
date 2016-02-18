package io.github.vyo.twig.logger

import org.slf4j.ILoggerFactory
import org.slf4j.spi.LoggerFactoryBinder

/**
 * Created by Manuel Weidmann on 18.02.16.
 */
object SLF4JBinder : LoggerFactoryBinder {

    public var REQUESTED_API_VERSION: String = "1.7.16"

    private val factory: SLF4JFactory = SLF4JFactory()

    override fun getLoggerFactory(): ILoggerFactory? {
        return factory
    }

    override fun getLoggerFactoryClassStr(): String? {
        return factory.javaClass.name
    }
}
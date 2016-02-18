package io.github.vyo.twig.logger

import org.slf4j.ILoggerFactory
import org.slf4j.Logger

/**
 * Created by Manuel Weidmann on 18.02.16.
 */
class SLF4JFactory : ILoggerFactory {
    override fun getLogger(name: String?): Logger? {

        if (name == null) throw IllegalArgumentException()

        val logger: io.github.vyo.twig.logger.Logger = io.github.vyo.twig.logger.Logger(name)
        val adapter: SLF4JAdapter = SLF4JAdapter()
        adapter.initialiseTwig(logger)
        return adapter
    }
}
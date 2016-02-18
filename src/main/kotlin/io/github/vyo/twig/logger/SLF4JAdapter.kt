package io.github.vyo.twig.logger

import org.slf4j.Logger
import org.slf4j.Marker

/**
 * Created by Manuel Weidmann on 18.02.16.
 */
class SLF4JAdapter : Logger {

    lateinit var twig: io.github.vyo.twig.logger.Logger

    fun initialiseTwig(twig: io.github.vyo.twig.logger.Logger) {
        this.twig = twig
    }

    override fun warn(msg: String?) {
        twig.warn(msg!!)
    }

    override fun warn(format: String?, arg: Any?) {
        twig.warn(arg!!)
    }

    override fun warn(format: String?, vararg arguments: Any?) {
        val args: MutableList<Pair<String, Any>> = arrayListOf()
        var index: Int = 1
        for (argument in arguments) {
            args.add(Pair("${index++}", argument!!))
        }

        twig.warn("", *args.toTypedArray())
    }

    override fun warn(format: String?, arg1: Any?, arg2: Any?) {
        twig.warn(Pair("arg1", arg1!!), Pair("arg2", arg2!!))
    }

    override fun warn(msg: String?, t: Throwable?) {
        twig.warn(msg!!, Pair("throwable", t!!))
    }

    override fun warn(marker: Marker?, msg: String?) {
        twig.warn(msg!!)
    }

    override fun warn(marker: Marker?, format: String?, arg: Any?) {
        twig.warn(arg!!)
    }

    override fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        twig.warn(Pair("arg1", arg1!!), Pair("arg2", arg2!!))
    }

    override fun warn(marker: Marker?, format: String?, vararg arguments: Any?) {
        val args: MutableList<Pair<String, Any>> = arrayListOf()
        var index: Int = 1
        for (argument in arguments) {
            args.add(Pair("${index++}", argument!!))
        }

        twig.warn("", *args.toTypedArray())
    }

    override fun warn(marker: Marker?, msg: String?, t: Throwable?) {
        twig.warn(msg!!, Pair("throwable", t!!))
    }

    override fun getName(): String? {
        return twig.javaClass.name
    }

    override fun info(msg: String?) {
        twig.info(msg!!)
    }

    override fun info(format: String?, arg: Any?) {
        twig.info(arg!!)
    }

    override fun info(format: String?, vararg arguments: Any?) {
        val args: MutableList<Pair<String, Any>> = arrayListOf()
        var index: Int = 1
        for (argument in arguments) {
            args.add(Pair("${index++}", argument!!))
        }

        twig.info("", *args.toTypedArray())
    }

    override fun info(format: String?, arg1: Any?, arg2: Any?) {
        twig.info(Pair("arg1", arg1!!), Pair("arg2", arg2!!))
    }

    override fun info(msg: String?, t: Throwable?) {
        twig.info(msg!!, Pair("throwable", t!!))
    }

    override fun info(marker: Marker?, msg: String?) {
        twig.info(msg!!)
    }

    override fun info(marker: Marker?, format: String?, arg: Any?) {
        twig.info(arg!!)
    }

    override fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        twig.info(Pair("arg1", arg1!!), Pair("arg2", arg2!!))
    }

    override fun info(marker: Marker?, format: String?, vararg arguments: Any?) {
        val args: MutableList<Pair<String, Any>> = arrayListOf()
        var index: Int = 1
        for (argument in arguments) {
            args.add(Pair("${index++}", argument!!))
        }

        twig.info("", *args.toTypedArray())
    }

    override fun info(marker: Marker?, msg: String?, t: Throwable?) {
        twig.info(msg!!, Pair("throwable", t!!))
    }

    override fun isErrorEnabled(): Boolean {
        return twig.level == Level.ERROR
    }

    override fun isErrorEnabled(marker: Marker?): Boolean {
        return twig.level == Level.ERROR
    }

    override fun error(msg: String?) {
        twig.error(msg!!)
    }

    override fun error(format: String?, arg: Any?) {
        twig.error(arg!!)
    }

    override fun error(format: String?, vararg arguments: Any?) {
        val args: MutableList<Pair<String, Any>> = arrayListOf()
        var index: Int = 1
        for (argument in arguments) {
            args.add(Pair("${index++}", argument!!))
        }

        twig.error("", *args.toTypedArray())
    }

    override fun error(format: String?, arg1: Any?, arg2: Any?) {
        twig.error(Pair("arg1", arg1!!), Pair("arg2", arg2!!))
    }

    override fun error(msg: String?, t: Throwable?) {
        twig.error(msg!!, Pair("throwable", t!!))
    }

    override fun error(marker: Marker?, msg: String?) {
        twig.error(msg!!)
    }

    override fun error(marker: Marker?, format: String?, arg: Any?) {
        twig.error(arg!!)
    }

    override fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        twig.error(Pair("arg1", arg1!!), Pair("arg2", arg2!!))
    }

    override fun error(marker: Marker?, format: String?, vararg arguments: Any?) {
        val args: MutableList<Pair<String, Any>> = arrayListOf()
        var index: Int = 1
        for (argument in arguments) {
            args.add(Pair("${index++}", argument!!))
        }

        twig.error("", *args.toTypedArray())
    }

    override fun error(marker: Marker?, msg: String?, t: Throwable?) {
        twig.error(msg!!, Pair("throwable", t!!))
    }

    override fun isDebugEnabled(): Boolean {
        return twig.level == Level.DEBUG
    }

    override fun isDebugEnabled(marker: Marker?): Boolean {
        return twig.level == Level.DEBUG
    }

    override fun debug(msg: String?) {
        twig.debug(msg!!)
    }

    override fun debug(format: String?, arg: Any?) {
        twig.debug(arg!!)
    }

    override fun debug(format: String?, vararg arguments: Any?) {
        val args: MutableList<Pair<String, Any>> = arrayListOf()
        var index: Int = 1
        for (argument in arguments) {
            args.add(Pair("${index++}", argument!!))
        }

        twig.debug("", *args.toTypedArray())
    }

    override fun debug(format: String?, arg1: Any?, arg2: Any?) {
        twig.debug(Pair("arg1", arg1!!), Pair("arg2", arg2!!))
    }

    override fun debug(msg: String?, t: Throwable?) {
        twig.debug(msg!!, Pair("throwable", t!!))
    }

    override fun debug(marker: Marker?, msg: String?) {
        twig.debug(msg!!)
    }

    override fun debug(marker: Marker?, format: String?, arg: Any?) {
        twig.debug(arg!!)
    }

    override fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        twig.debug(Pair("arg1", arg1!!), Pair("arg2", arg2!!))
    }

    override fun debug(marker: Marker?, format: String?, vararg arguments: Any?) {
        val args: MutableList<Pair<String, Any>> = arrayListOf()
        var index: Int = 1
        for (argument in arguments) {
            args.add(Pair("${index++}", argument!!))
        }

        twig.debug("", *args.toTypedArray())
    }

    override fun debug(marker: Marker?, msg: String?, t: Throwable?) {
        twig.debug(msg!!, Pair("throwable", t!!))
    }

    override fun isInfoEnabled(): Boolean {
        return twig.level == Level.INFO
    }

    override fun isInfoEnabled(marker: Marker?): Boolean {
        return twig.level == Level.INFO
    }

    override fun trace(msg: String?) {
        twig.trace(msg!!)
    }

    override fun trace(format: String?, arg: Any?) {
        twig.trace(arg!!)
    }

    override fun trace(format: String?, vararg arguments: Any?) {
        val args: MutableList<Pair<String, Any>> = arrayListOf()
        var index: Int = 1
        for (argument in arguments) {
            args.add(Pair("${index++}", argument!!))
        }

        twig.trace("", *args.toTypedArray())
    }

    override fun trace(format: String?, arg1: Any?, arg2: Any?) {
        twig.trace(Pair("arg1", arg1!!), Pair("arg2", arg2!!))
    }

    override fun trace(msg: String?, t: Throwable?) {
        twig.trace(msg!!, Pair("throwable", t!!))
    }

    override fun trace(marker: Marker?, msg: String?) {
        twig.trace(msg!!)
    }

    override fun trace(marker: Marker?, format: String?, arg: Any?) {
        twig.trace(arg!!)
    }

    override fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        twig.trace(Pair("arg1", arg1!!), Pair("arg2", arg2!!))
    }

    override fun trace(marker: Marker?, format: String?, vararg arguments: Any?) {
        val args: MutableList<Pair<String, Any>> = arrayListOf()
        var index: Int = 1
        for (argument in arguments) {
            args.add(Pair("${index++}", argument!!))
        }

        twig.trace("", *args.toTypedArray())
    }

    override fun trace(marker: Marker?, msg: String?, t: Throwable?) {
        twig.trace(msg!!, Pair("throwable", t!!))
    }

    override fun isWarnEnabled(): Boolean {
        return twig.level == Level.WARN
    }

    override fun isWarnEnabled(marker: Marker?): Boolean {
        return twig.level == Level.WARN
    }

    override fun isTraceEnabled(): Boolean {
        return twig.level == Level.TRACE
    }

    override fun isTraceEnabled(marker: Marker?): Boolean {
        return twig.level == Level.TRACE
    }
}
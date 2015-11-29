package io.github.vyo.twig.logger

import io.github.vyo.twig.appender.Appender
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.shouldBeTrue
import org.jetbrains.spek.api.shouldEqual
import java.util.*

/**
 * Created by Manuel Weidmann on 28.11.2015.
 */
class LoggerSpec : Spek() {
    companion object DummyLog {
        private class StringAppender : Appender {
            var log: ArrayList<String> = arrayListOf()

            override fun write(logEntry: String) {
                log.add(logEntry)
            }
        }

        val appender: StringAppender = StringAppender()

        var entries: ArrayList<String> = appender.log
            get() {
                return appender.log
            }

        fun size(): Int {
            return entries.size
        }

        fun clear() {
            entries.clear()
        }

        override fun toString(): String {
            return entries.toString()
        }

    }

    init {
        Logger.root.appender = DummyLog.appender

        given("nothing") {

            beforeOn {
                DummyLog.clear()
            }

            on("initialisation") {
                it("should setup root logger log threshold") {
                    shouldEqual(Level.INFO, Logger.threshold)
                }
            }

            on("invocation") {
                it("should be able to log messages as 'TwigRootLogger'") {
                    Logger.info("root test message").get()

                    shouldBeTrue(DummyLog.entries[0].contains("TwigRootLogger") && DummyLog.entries[0].contains("root test " +
                            "message"))
                }
            }

            on("changing the root log level") {
                it("should log the change") {

                }
            }

            on("changing the root appender") {
                it ("should log the change") {

                }
            }
        }

        given("a logger") {

            beforeOn {
                DummyLog.clear()
            }

            val customLogger: Logger = Logger("customTestLogger")
            on("invocation with custom field audit and value classified") {
                it("should add the custom field to the JSON log entry") {
                    DummyLog.clear()
                    customLogger.info("some test message", Pair("audit", "classified"), Pair("retention", "none")).get()

                    shouldBeTrue(DummyLog.entries[0].contains("classified") && DummyLog.entries[0].contains
                    ("customTestLogger"))
                }
            }
        }

    }
}
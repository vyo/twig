package io.github.vyo.twig.logger

import io.github.vyo.twig.appender.Appender
import nl.komponents.kovenant.async
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.shouldBeTrue
import org.jetbrains.spek.api.shouldEqual
import java.util.*
import kotlin.test.fail

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

            on("initialisation") {
                it("should setup root logger log threshold") {
                    shouldEqual(Level.INFO, Logger.threshold)
                }
            }

            on("invocation") {
                it("should be able to log messages as 'TwigRootLogger'") {
                    async {
                        Logger.info("root test message")

                        var retriesLeft: Int = 5
                        while (retriesLeft != 0) {
                            var entryFound: Boolean = false
                            for (entry in DummyLog.entries) {
                                if (entry.contains("TwigRootLogger") && entry.contains("root test message"))
                                    entryFound = true
                            }
                            println(DummyLog)
                            shouldBeTrue(entryFound)
                            Thread.sleep(5)
                            retriesLeft--
                        }
                        if (retriesLeft == 0) fail("failed to write to log")
                    } fail {
                        fail(it.message)
                    }
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

        given("a logger with a custom field name \'custom\'") {

            //            beforeOn {
            //                DummyLog.clear()
            //            }

            val customLogger: Logger = Logger("customTestLogger", customFields = arrayOf("custom", "audit",
                    "retention"))
            on("invocation") {
                it("should add the custom field to the JSON log entry") {
                    async {
                        customLogger.info("some test message", "", "classified", "none")

                        var retriesLeft: Int = 5
                        while (retriesLeft != 0) {
                            var entryFound: Boolean = false
                            for (entry in DummyLog.entries) {
                                if (entry.contains("classified") && entry.contains("customTestLogger")) entryFound =
                                        true
                            }
                            println(DummyLog)
                            shouldBeTrue(entryFound)
                            Thread.sleep(5)
                            retriesLeft--
                        }
                        if (retriesLeft == 0) fail("failed to write to log")
                    } fail {
                        fail(it.message)
                    }
                }
            }
        }

    }
}
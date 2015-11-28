package io.github.vyo.twig.logger

import io.github.vyo.twig.appender.Appender
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.shouldBeTrue
import org.jetbrains.spek.api.shouldEqual
import java.util.*
import kotlin.test.fail

/**
 * Created by Manuel Weidmann on 28.11.2015.
 */
class LoggerSpec : Spek() {
    init {
        given("nothing") {
            on("initialisation") {
                it("should setup root logger log threshold") {
                    shouldEqual(Level.INFO, Logger.threshold)
                }
            }
            on("invocation") {
                it("should be able to log messages as 'root'") {
                    Logger.info("logging test $this")
                }
            }
        }
        given("a custom field name \'custom\'") {
            var log: ArrayList<String> = arrayListOf()

            class StringAppender : Appender {
                override fun write(logEntry: String) {
                    println(logEntry)
                    log.add(logEntry)
                }

            }

            val customLogger: Logger = Logger("customTestLogger", StringAppender(), arrayOf("custom", "audit",
                    "retention"))
            on("invocation") {
                it("should add the custom field to the JSON log entry") {
                    customLogger.info("some test message", "", "classified", "none")

                    var retriesLeft: Int = 5
                    while (log.size == 0 && retriesLeft != 0) {
                        Thread.sleep(1)
                        retriesLeft--
                    }
                    if (retriesLeft == 0) fail("failed to write to log")

                    shouldBeTrue(log.get(0).contains("classified"))
                }
            }
        }

    }
}
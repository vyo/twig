package io.github.vyo.twig.logger

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.shouldEqual

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
            val customLogger: Logger = Logger("customTestLogger", arrayOf("custom", "audit", "retention"))
            on("invocation") {
                it("should add the custom field to the JSON log entry") {
                    customLogger.info("some test message", "", "classified", "none")
                }
            }
        }

    }
}
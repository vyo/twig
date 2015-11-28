package io.github.vyo.twig

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
                    shouldEqual(Level.INFO, Logger.root.threshold)
                }
            }
            on("invocation") {
                it("should be able to log messages as 'root'") {
                    Logger.root.info("logging test $this")
                }
            }
        }

    }
}
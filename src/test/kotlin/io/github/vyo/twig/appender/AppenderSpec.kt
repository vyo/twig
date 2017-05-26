package io.github.vyo.twig.appender

import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * Created by Manuel Weidmann <manuel.weidmann@7p-group.com> on 26.05.17.
 */
object AppenderSpec : Spek({
    val entry = "{some random string}"
    val byteStream = ByteArrayOutputStream()
    val printStream = PrintStream(byteStream)
    val originalStream = System.out
    System.setOut(printStream)

    describe("a console appender") {
        val appender = ConsoleAppender()

        on("writing") {
            appender.write(entry)

            it("should write to system.out as is and append a line separator") {
                "$entry${System.lineSeparator()}".should.equal(byteStream.toString(Charsets.UTF_8.name()))
            }
        }
    }
})
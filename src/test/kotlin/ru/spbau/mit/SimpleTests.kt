package ru.spbau.mit

import org.junit.Test
import kotlin.test.assertEquals

internal class SimpleTests {
    @Test
    fun simpleTest1() {
        val rows = listOf(1, 22, 333)
        val code = """
                |\documentclass{beamer}
                |\usepackage{babel}[russian]
                |\begin{frame}[arg1=arg2]
                |  \frametitile{frametitle}
                |  \begin{itemize}
                |      \item
                |      1 text
                |
                |      \item
                |      22 text
                |
                |      \item
                |      333 text
                |
                |  \end{itemize}
                |  \begin{pyglist}[language=kotlin]
                |    val a = 1
                |    foo()
                |  \end{pyglist}
                |\end{frame}
            """.trimMargin()
        val dsl = document {
            documentClass("beamer")
            usePackage("babel", "russian" /* varargs */)
            frame("arg1" to "arg2", frameTitle="frametitle") {
                itemize {
                    for (row in rows) {
                        item { + "$row text" }
                    }
                }

                // begin{pyglist}[language=kotlin]...\end{pyglist}
                customTag(name = "pyglist", options = "language" to "kotlin") {
                    +"""
                        |val a = 1
                        |foo()
                    """.trimMargin()
                }
            }
        }
        val text = StringBuilder()
                .also { dsl.render(it, "") }
                .toString()
        assertEquals(code, text)
    }
}
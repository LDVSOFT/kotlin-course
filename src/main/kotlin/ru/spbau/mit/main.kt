package ru.spbau.mit

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams

fun main(args: Array<String>) {
    val lexer = LanguageLexer(CharStreams.fromString("var x"))
    val parser = LanguageParser(BufferedTokenStream(lexer)).file()
    println(parser)
}
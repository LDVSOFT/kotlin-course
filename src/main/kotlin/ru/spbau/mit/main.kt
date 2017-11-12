package ru.spbau.mit

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams

fun main(args: Array<String>) {
    val lexer = LanguageLexer(CharStreams.fromString("var x = 12 + foo(bar())"))
    val parsedFile = LanguageParser(BufferedTokenStream(lexer)).file()
    println(translate(parsedFile))
}
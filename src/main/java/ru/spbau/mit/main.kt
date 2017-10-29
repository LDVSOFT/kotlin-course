package ru.spbau.mit

fun main(args: Array<String>) {
    val input = readLine()!!
    val isOk = SentenceParser.parse(input) != null
    if (isOk)
        println("YES")
    else
        println("NO")
}

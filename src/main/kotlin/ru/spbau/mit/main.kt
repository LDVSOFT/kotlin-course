package ru.spbau.mit

import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("Wrong number of arguments")
        exitProcess(1)
    }

    parse(Paths.get(args[0])).eval()
}
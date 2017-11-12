package ru.spbau.mit

internal val Int.l get() = Literal(this)
internal val String.v get() = Variable(this)


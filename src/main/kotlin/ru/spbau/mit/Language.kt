package ru.spbau.mit

data class Program(
        val block: Block
)

data class Block(
        val body: List<Statement>
) {
    constructor(vararg statements: Statement): this(statements.toList())
}
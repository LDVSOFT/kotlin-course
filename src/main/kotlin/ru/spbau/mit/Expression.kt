package ru.spbau.mit

sealed class Expression {
    interface Visitor<out T> {
        fun visit(functionCall: FunctionCall): T
        fun visit(binary: BinaryExpression): T
        fun visit(literal: Literal): T
        fun visit(variable: Variable): T
    }

    abstract fun <T> visit(visitor: Visitor<T>): T
}

data class FunctionCall(
        val name: String,
        val args: List<Expression>
): Expression() {
    constructor(name: String, vararg args: Expression): this(name, args.toList())

    override fun <T> visit(visitor: Visitor<T>) = visitor.visit(this)
}

enum class Operator(val text: String) {
    OR ("||"),
    AND("&&"),
    EQL("=="),
    NEQ("!="),
    LET("<"),
    GRT(">"),
    LEQ("<="),
    GRQ(">="),
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    MOD("%")
}

data class BinaryExpression(
        val left: Expression,
        val right: Expression,
        val op: Operator
): Expression() {
    override fun <T> visit(visitor: Visitor<T>) = visitor.visit(this)
}

data class Literal(
        val value: Int
): Expression() {
    override fun <T> visit(visitor: Visitor<T>) = visitor.visit(this)
}

data class Variable(
        val name: String
): Expression() {
    override fun <T> visit(visitor: Visitor<T>) = visitor.visit(this)
}
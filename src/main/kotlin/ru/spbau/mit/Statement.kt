package ru.spbau.mit

sealed class Statement {
    interface Visitor<T> {
        fun visit(function: FunctionDefinition): T
        fun visit(variable: VariableDefinition): T
        fun visit(expression: ExpressionStatement): T
        fun visit(whileStatement: WhileStatement): T
        fun visit(ifStatement: IfStatement): T
        fun visit(assignment: AssignmentStatement): T
        fun visit(returnStatement: ReturnStatement): T
    }

    abstract fun <T> visit(visitor: Visitor<T>): T
}

data class FunctionDefinition(
        val name: String,
        val args: List<String>,
        val body: Block
): Statement() {
    override fun <T> visit(visitor: Visitor<T>) = visitor.visit(this)
}

data class VariableDefinition(
        val name: String,
        val init: Expression?
): Statement() {
    override fun <T> visit(visitor: Visitor<T>) = visitor.visit(this)
}

data class ExpressionStatement(
        val e: Expression
): Statement() {
    override fun <T> visit(visitor: Visitor<T>) = visitor.visit(this)
}


data class WhileStatement(
        val condition: Expression,
        val body: Block
): Statement() {
    override fun <T> visit(visitor: Visitor<T>) = visitor.visit(this)
}

data class IfStatement(
        val condition: Expression,
        val thenBranch: Block,
        val elseBranch: Block?
): Statement() {
    override fun <T> visit(visitor: Visitor<T>) = visitor.visit(this)
}

data class AssignmentStatement(
        val name: String,
        val expression: Expression
): Statement() {
    override fun <T> visit(visitor: Visitor<T>) = visitor.visit(this)
}

data class ReturnStatement(
        val expression: Expression
): Statement() {
    override fun <T> visit(visitor: Visitor<T>) = visitor.visit(this)
}
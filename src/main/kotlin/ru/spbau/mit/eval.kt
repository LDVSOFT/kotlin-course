package ru.spbau.mit

object Builtins {
    fun println(args: List<Int>): Int {
        println(args.joinToString())
        return 0
    }

    val MAP = mapOf(
            "println" to Builtins::println
    )
}

private val defaultScope = Scope(null, funcs = Builtins.MAP)

fun Program.eval(baseScope: Scope = defaultScope): Int {
    val visitor = EvalVisitor(baseScope)
    return visitor.visitBlock(block)
}

fun Expression.eval(baseScope: Scope = defaultScope): Int {
    val visitor = EvalVisitor(baseScope)
    return visit(visitor)
}

private data class EvalVisitor(val scope: Scope): Statement.Visitor<Unit>, Expression.Visitor<Int> {
    private var returnValue = null as Int?

    internal fun visitBlock(block: Block): Int {
        block.body.forEach {
            it.visit(this)
            val returnedValue = returnValue
            if (returnedValue != null) {
                return returnedValue
            }
        }
        return 0
    }

    private fun Block.visit(): Int = visitBlock(this)

    override fun visit(functionCall: FunctionCall): Int {
        with(functionCall) {
            val function = scope.lookupAndGetFunction(name)
                    ?: throw EvaluationException("Function $name not in scope")
            val args = args.map { it.visit(this@EvalVisitor) }
            return function.invoke(args)
        }
    }

    override fun visit(binary: BinaryExpression): Int {
        val left = binary.left.visit(this)
        val right = binary.right.visit(this)
        return when(binary.op) {
            Operator.OR  -> if ((left != 0) || (right != 0)) 1 else 0
            Operator.AND -> if ((left != 0) && (right != 0)) 1 else 0
            Operator.EQL -> if (left == right) 1 else 0
            Operator.NEQ -> if (left != right) 1 else 0
            Operator.LET -> if (left <  right) 1 else 0
            Operator.GRT -> if (left >  right) 1 else 0
            Operator.LEQ -> if (left <= right) 1 else 0
            Operator.GRQ -> if (left >= right) 1 else 0
            Operator.ADD -> left + right
            Operator.SUB -> left - right
            Operator.MUL -> left * right
            Operator.DIV -> left / right
            Operator.MOD -> left % right
        }
    }

    override fun visit(literal: Literal): Int = literal.value

    override fun visit(variable: Variable): Int = scope.lookupAndGetVariable(variable.name)
            ?: throw EvaluationException("Variable ${variable.name} not in scope")

    override fun visit(function: FunctionDefinition) {
        if (!scope.addFunction(function.name, UserFunction(function))) {
            throw EvaluationException("Function duplicated in scope: ${function.name}")
        }
    }

    override fun visit(variable: VariableDefinition) {
        if (!scope.addVariable(variable.name, variable.init?.visit(this))) {
            throw EvaluationException("Variable duplicated in scope :${variable.name}")
        }
    }

    override fun visit(expression: ExpressionStatement) {
        expression.e.visit(this)
    }

    override fun visit(whileStatement: WhileStatement) {
        with(whileStatement) {
            while (returnValue == null && condition.visit(this@EvalVisitor) != 0) {
                body.visit()
            }
        }
    }

    override fun visit(ifStatement: IfStatement) {
        with(ifStatement) {
            if (condition.visit(this@EvalVisitor) != 0) {
                thenBranch.visit()
            } else {
                elseBranch?.visit()
            }
        }
    }

    override fun visit(assignment: AssignmentStatement) {
        with(assignment) {
            if (!scope.lookupAndSetVariable(name, expression.visit(this@EvalVisitor))) {
                throw EvaluationException("Variable $name not in scope")
            }
        }
    }

    override fun visit(returnStatement: ReturnStatement) {
        if (returnValue != null) {
            throw EvaluationException("Double return")
        }
        returnValue = returnStatement.expression.visit(this)
    }

    private inner class UserFunction(
            val function: FunctionDefinition
    ): Function {
        override fun invoke(args: List<Int>): Int {
            if (args.size != function.args.size) {
                throw EvaluationException("Cannot call function ${function.name}: requires ${function.args.size}")
            }
            val newScope = Scope(scope, function.args.zip(args).toMap())
            return EvalVisitor(newScope).visitBlock(function.body)
        }
    }
}

class EvaluationException(message: String, cause: Throwable? = null): Exception(message, cause)
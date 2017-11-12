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

fun rootScopeOf(vars: Map<String, Int> = emptyMap(), funcs: Map<String, Function> = emptyMap()) =
        Scope(null, FunctionScope(), vars, funcs)

private val defaultScope = rootScopeOf(funcs = Builtins.MAP)

fun Program.eval(baseScope: Scope = defaultScope): Int {
    val visitor = EvalVisitor(baseScope)
    return visitor.visitBlock(block)
}

fun Expression.eval(baseScope: Scope = defaultScope): Int {
    val visitor = EvalVisitor(baseScope)
    return visit(visitor)
}

private data class EvalVisitor(val scope: Scope): Statement.Visitor<Unit>, Expression.Visitor<Int> {
    internal fun visitFunctionBlock(block: Block, vars: Map<String, Int> = emptyMap()): Int {
        val newScope = Scope(scope, FunctionScope(), vars)
        return EvalVisitor(newScope).visitBlock(block)
    }

    internal fun visitBlockScoped(block: Block, vars: Map<String, Int> = emptyMap()): Int {
        val newScope = Scope(scope, vars)
        return EvalVisitor(newScope).visitBlock(block)
    }

    internal fun visitBlock(block: Block): Int {
        block.body.forEach {
            it.visit(this)
            val returnedValue = scope.functionScope.returned
            if (returnedValue != null) {
                return returnedValue
            }
        }
        return 0
    }

    private fun Block.visitScoped(): Int = visitBlockScoped(this)

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
            throw EvaluationException("Variable duplicated in scope: ${variable.name}")
        }
    }

    override fun visit(expression: ExpressionStatement) {
        expression.e.visit(this)
    }

    override fun visit(whileStatement: WhileStatement) {
        with(whileStatement) {
            while (!scope.returned && condition.visit(this@EvalVisitor) != 0) {
                body.visitScoped()
            }
        }
    }

    override fun visit(ifStatement: IfStatement) {
        with(ifStatement) {
            if (condition.visit(this@EvalVisitor) != 0) {
                thenBranch.visitScoped()
            } else {
                elseBranch?.visitScoped()
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
        if (scope.returned) {
            throw EvaluationException("Double return")
        }
        scope.functionScope.returnWith(returnStatement.expression.visit(this))
    }

    private inner class UserFunction(
            val function: FunctionDefinition
    ): Function {
        override fun invoke(args: List<Int>): Int {
            if (args.size != function.args.size) {
                throw EvaluationException("Cannot call function ${function.name}: requires ${function.args.size}")
            }
            return visitFunctionBlock(function.body, function.args.zip(args).toMap())
        }
    }
}

class EvaluationException(message: String, cause: Throwable? = null): Exception(message, cause)
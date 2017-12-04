package ru.spbau.mit

object Builtins {
    private fun println(args: List<Int>): Int {
        println(args.joinToString())
        return args.size
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

private inline fun <T> T?.mustBeNonNull(errorMessage: () -> String): T
        = this ?: throw EvaluationException(errorMessage())

private inline fun Boolean.mustBeTrue(errorMessage: () -> String) {
    if (!this) throw EvaluationException(errorMessage())
}

private inline fun mustBeFalse(value: Boolean, errorMessage: () -> String) {
    if (value) throw EvaluationException(errorMessage())
}

private class EvalVisitor(val scope: Scope): Statement.Visitor<Unit>, Expression.Visitor<Int> {
    internal fun visitFunctionBlock(block: Block, vars: Map<String, Int> = emptyMap()): Int {
        val newScope = Scope(scope, FunctionScope(), vars)
        return EvalVisitor(newScope).visitBlock(block)
    }

    internal fun visitBlockScoped(block: Block, vars: Map<String, Int> = emptyMap()): Int {
        val newScope = Scope(scope, vars)
        return EvalVisitor(newScope).visitBlock(block)
    }

    internal fun visitBlock(block: Block): Int {
        for (statement in block.body) {
            statement.visit(this)
            val returnedValue = scope.functionScope.returned
            if (returnedValue != null) {
                return returnedValue
            }
        }
        return 0
    }

    private fun Block.visitScoped(): Int = visitBlockScoped(this)

    override fun visit(functionCall: FunctionCall): Int {
        val function = scope.lookupAndGetFunction(functionCall.name)
                .mustBeNonNull { "Function ${functionCall.name} not in scope" }
        val args = functionCall.args.map { it.visit(this) }
        return function.invoke(args)
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

    override fun visit(variable: Variable): Int
            = scope.lookupAndGetVariable(variable.name).mustBeNonNull { "Variable ${variable.name} not in scope" }

    override fun visit(function: FunctionDefinition) {
        scope.addFunction(function.name, UserFunction(function))
                .mustBeTrue { "Function duplicated in scope: ${function.name}" }
    }

    override fun visit(variable: VariableDefinition) {
        scope.addVariable(variable.name, variable.init?.visit(this))
                .mustBeTrue { "Variable duplicated in scope: ${variable.name}" }
    }

    override fun visit(expression: ExpressionStatement) {
        expression.e.visit(this)
    }

    override fun visit(whileStatement: WhileStatement) {
        while (!scope.returned && whileStatement.condition.visit(this) != 0) {
            whileStatement.body.visitScoped()
        }
    }

    override fun visit(ifStatement: IfStatement) {
        if (ifStatement.condition.visit(this) != 0) {
            ifStatement.thenBranch.visitScoped()
        } else {
            ifStatement.elseBranch?.visitScoped()
        }
    }

    override fun visit(assignment: AssignmentStatement) {
        scope.lookupAndSetVariable(assignment.name, assignment.expression.visit(this))
                .mustBeTrue { "Variable ${assignment.name} not in scope" }
    }

    override fun visit(returnStatement: ReturnStatement) {
        mustBeFalse(scope.returned) { "Double return" }
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
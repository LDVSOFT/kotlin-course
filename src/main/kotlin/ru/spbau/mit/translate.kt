package ru.spbau.mit

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.misc.ParseCancellationException
import org.antlr.v4.runtime.tree.*
import ru.spbau.mit.LanguageParser.*
import java.nio.file.Path

fun translate(program: FileContext): Program {
    return TranslateVisitor.visitFile(program)
}

private fun tryParse(stream: CharStream): FileContext {
    try {
        val lexer = LanguageLexer(stream).apply {
            removeErrorListeners()
            addErrorListener(ThrowingErrorHandler)
        }
        val parser = LanguageParser(BufferedTokenStream(lexer)).apply {
            removeErrorListeners()
            addErrorListener(ThrowingErrorHandler)
        }
        return parser.file()
    } catch (e: ParseCancellationException) {
        throw ParsingException("Failed to parse the program", e)
    }
}

fun parse(text: String): Program {
    return translate(tryParse(CharStreams.fromString(text)))
}

fun parse(path: Path): Program {
    return translate(tryParse(CharStreams.fromPath(path)))
}

private object TranslateVisitor: AbstractParseTreeVisitor<Any>(), LanguageVisitor<Any> {
    private val OPERATORS = Operator.values().map { Pair(it.text, it) }.toMap()

    private fun String.toOperator(): Operator =
            OPERATORS[this] ?: throw IllegalArgumentException("Unknown operator from parser")

    private fun <T1, T2> visitOperatorExpression(sole: T1?, left: T1?, right: T2?, op: Token?): Expression
            where T1 : ParserRuleContext, T2 : ParserRuleContext{
        if (sole != null) {
            return sole.accept(this) as Expression
        }
        return BinaryExpression(
                left!!.accept(this) as Expression,
                right!!.accept(this) as Expression,
                op!!.text.toOperator()
        )
    }

    // Blocks

    override fun visitFile(ctx: FileContext): Program {
        return Program(visitBlock(ctx.b))
    }

    override fun visitBlock(ctx: BlockContext): Block {
        return Block(ctx.statement().map { visitStatement(it) })
    }

    override fun visitBlockWithBraces(ctx: BlockWithBracesContext): Block {
        return visitBlock(ctx.block())
    }

    override fun visitIdentifier(ctx: IdentifierContext): String = ctx.text

    // Statements

    override fun visitStatement(ctx: StatementContext) = visitChildren(ctx) as Statement

    override fun visitFunctionDefinition(ctx: FunctionDefinitionContext) = FunctionDefinition(
            visitIdentifier(ctx.name),
            visitParameterNames(ctx.parameterNames()),
            visitBlockWithBraces(ctx.body)
    )

    override fun visitVariableDefinition(ctx: VariableDefinitionContext) = VariableDefinition(
            visitIdentifier(ctx.name),
            if (ctx.init != null) visitExpression(ctx.init) else null
    )

    override fun visitParameterNames(ctx: ParameterNamesContext) =
            ctx.identifier().map { visitIdentifier(it) }

    override fun visitWhileStatement(ctx: WhileStatementContext): WhileStatement {
        return WhileStatement(
                visitExpression(ctx.condition),
                visitBlockWithBraces(ctx.body)
        )
    }

    override fun visitIfStatement(ctx: IfStatementContext): IfStatement {
        return IfStatement(
                visitExpression(ctx.condition),
                visitBlockWithBraces(ctx.thenBranch),
                ctx.elseBranch?.let { visitBlockWithBraces(it) }
        )
    }

    override fun visitAssignment(ctx: AssignmentContext): AssignmentStatement {
        return AssignmentStatement(
                visitIdentifier(ctx.name),
                visitExpression(ctx.value)
        )
    }

    override fun visitReturnStatement(ctx: ReturnStatementContext): ReturnStatement {
        return ReturnStatement(
                visitExpression(ctx.value)
        )
    }

    override fun visitExpressionStatement(ctx: ExpressionStatementContext) = ExpressionStatement(
            visitExpression(ctx.e)
    )

    // Expressions

    override fun visitExpression(ctx: ExpressionContext) = visitChildren(ctx) as Expression

    override fun visitBaseExpression(ctx: BaseExpressionContext) = visitChildren(ctx) as Expression

    override fun visitBracedExpression(ctx: BracedExpressionContext) = visitExpression(ctx.braced)

    override fun visitOrExpression(ctx: OrExpressionContext) =
            visitOperatorExpression(ctx.sole, ctx.left, ctx.right, ctx.op)

    override fun visitAndExpression(ctx: AndExpressionContext) =
            visitOperatorExpression(ctx.sole, ctx.left, ctx.right, ctx.op)

    override fun visitEqExpression(ctx: EqExpressionContext) =
            visitOperatorExpression(ctx.sole, ctx.left, ctx.right, ctx.op)

    override fun visitCmpExpression(ctx: CmpExpressionContext) =
            visitOperatorExpression(ctx.sole, ctx.left, ctx.right, ctx.op)

    override fun visitAddExpression(ctx: AddExpressionContext) =
            visitOperatorExpression(ctx.sole, ctx.left, ctx.right, ctx.op)

    override fun visitMulExpression(ctx: MulExpressionContext) =
            visitOperatorExpression(ctx.sole, ctx.left, ctx.right, ctx.op)

    override fun visitLiteral(ctx: LiteralContext) = Literal(ctx.text.toInt())

    override fun visitVariable(ctx: VariableContext) = Variable(
            visitIdentifier(ctx.name)
    )

    override fun visitFunctionCall(ctx: FunctionCallContext) = FunctionCall(
            visitIdentifier(ctx.name),
            visitArguments(ctx.args)
    )

    override fun visitArguments(ctx: ArgumentsContext) = ctx.expression().map { visitExpression(it) }
}

class ParsingException(message: String, cause: Throwable? = null): Exception(message, cause)

object ThrowingErrorHandler : BaseErrorListener() {
    override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
        throw ParseCancellationException("Line $line, char $charPositionInLine: $msg")
    }
}
package ru.spbau.mit

import org.junit.Assert.*
import org.junit.Test

internal class EvalTest {
    private val Int.l get() = Literal(this)
    private val String.v get() = Variable(this)

    private val evalLog = mutableListOf<Int>()

    private val baseScope = Scope(null, funcs = mapOf(
            "log" to { args: List<Int> ->
                evalLog.add(args[0])
                args[0]
            })
    )

    @Test
    fun arithmeticOperatorsTest() {
        assertEquals(26, BinaryExpression(12.l, 14.l, Operator.ADD).eval())
        assertEquals(-2, BinaryExpression(12.l, 14.l, Operator.SUB).eval())

        assertEquals(45, BinaryExpression(5.l, 9.l, Operator.MUL).eval())
        assertEquals(5, BinaryExpression(45.l, 9.l, Operator.DIV).eval())
        assertEquals(0, BinaryExpression(45.l, 9.l, Operator.MOD).eval())
        assertEquals(4, BinaryExpression(40.l, 9.l, Operator.DIV).eval())
        assertEquals(4, BinaryExpression(40.l, 9.l, Operator.MOD).eval())
    }

    @Test
    fun comparisionOperatorsTest() {
        assertEquals(0, BinaryExpression(13.l, 14.l, Operator.EQL).eval())
        assertEquals(0, BinaryExpression(14.l, 13.l, Operator.EQL).eval())
        assertEquals(1, BinaryExpression(13.l, 13.l, Operator.EQL).eval())

        assertEquals(1, BinaryExpression(13.l, 14.l, Operator.NEQ).eval())
        assertEquals(1, BinaryExpression(14.l, 13.l, Operator.NEQ).eval())
        assertEquals(0, BinaryExpression(13.l, 13.l, Operator.NEQ).eval())

        assertEquals(1, BinaryExpression(13.l, 14.l, Operator.LET).eval())
        assertEquals(0, BinaryExpression(14.l, 13.l, Operator.LET).eval())
        assertEquals(0, BinaryExpression(13.l, 13.l, Operator.LET).eval())

        assertEquals(1, BinaryExpression(13.l, 14.l, Operator.LEQ).eval())
        assertEquals(0, BinaryExpression(14.l, 13.l, Operator.LEQ).eval())
        assertEquals(1, BinaryExpression(13.l, 13.l, Operator.LEQ).eval())


        assertEquals(0, BinaryExpression(13.l, 14.l, Operator.GRT).eval())
        assertEquals(1, BinaryExpression(14.l, 13.l, Operator.GRT).eval())
        assertEquals(0, BinaryExpression(13.l, 13.l, Operator.GRT).eval())

        assertEquals(0, BinaryExpression(13.l, 14.l, Operator.GRQ).eval())
        assertEquals(1, BinaryExpression(14.l, 13.l, Operator.GRQ).eval())
        assertEquals(1, BinaryExpression(13.l, 13.l, Operator.GRQ).eval())
    }

    @Test
    fun logicOperatorsTest() {
        assertEquals(0, BinaryExpression(0.l, 0.l, Operator.AND).eval())
        assertEquals(0, BinaryExpression(1.l, 0.l, Operator.AND).eval())
        assertEquals(0, BinaryExpression(0.l, 1.l, Operator.AND).eval())
        assertEquals(1, BinaryExpression(1.l, 1.l, Operator.AND).eval())


        assertEquals(0, BinaryExpression(0.l, 0.l, Operator.OR).eval())
        assertEquals(1, BinaryExpression(1.l, 0.l, Operator.OR).eval())
        assertEquals(1, BinaryExpression(0.l, 1.l, Operator.OR).eval())
        assertEquals(1, BinaryExpression(1.l, 1.l, Operator.OR).eval())
    }

    @Test
    fun evaluationOrderTest() {
        Operator.values().forEach { op ->
            evalLog.clear()
            val expr = BinaryExpression(
                    FunctionCall("log", listOf(1.l)),
                    FunctionCall("log", listOf(2.l)),
                    op
            )
            expr.eval(baseScope)
            assertEquals(listOf(1, 2), evalLog)
        }
    }

    @Test
    fun simpleProgram() {
        val program = Program(Block(listOf(
                VariableDefinition("x", 1.l),
                VariableDefinition("n", 1.l),
                WhileStatement(BinaryExpression("n".v, 5.l, Operator.LEQ), Block(listOf(
                        AssignmentStatement("x", BinaryExpression("x".v, "n".v, Operator.MUL)),
                        AssignmentStatement("n", BinaryExpression("n".v, 1.l, Operator.ADD))
                ))),
                ExpressionStatement(FunctionCall("log", listOf("x".v)))
        )))
        program.eval(baseScope)
        assertEquals(listOf(120), evalLog)
    }

    @Test
    fun customFunctionProgram() {
        val program = Program(Block(listOf(
                FunctionDefinition("factorial", listOf("n"), Block(listOf(
                        VariableDefinition("x", 1.l),
                        VariableDefinition("i", 1.l),
                        WhileStatement(BinaryExpression("i".v, "n".v, Operator.LEQ), Block(listOf(
                                AssignmentStatement("x", BinaryExpression("x".v, "i".v, Operator.MUL)),
                                AssignmentStatement("i", BinaryExpression("i".v, 1.l, Operator.ADD))
                        ))),
                        ReturnStatement("x".v)
                ))),
                VariableDefinition("i", 1.l),
                WhileStatement(BinaryExpression("i".v, 5.l, Operator.LEQ), Block(listOf(
                        ExpressionStatement(FunctionCall("log", listOf(
                                FunctionCall("factorial", listOf("i".v))
                        ))),
                        AssignmentStatement("i", BinaryExpression("i".v, 1.l, Operator.ADD))
                )))
        )))
        program.eval(baseScope)
        assertEquals(listOf(1, 2, 6, 24, 120), evalLog)
    }

    @Test
    fun recursionAndReturnTest() {
        val program = Program(Block(listOf(
                FunctionDefinition("factorial", listOf("n"), Block(listOf(
                        IfStatement(BinaryExpression("n".v, 1.l, Operator.LEQ), Block(listOf(
                                ReturnStatement(1.l)
                        )), null),
                        ReturnStatement(BinaryExpression("n".v, FunctionCall("factorial", listOf(
                                BinaryExpression("n".v, 1.l, Operator.SUB)
                        )), Operator.MUL))
                ))),
                VariableDefinition("i", 1.l),
                WhileStatement(BinaryExpression("i".v, 5.l, Operator.LEQ), Block(listOf(
                        ExpressionStatement(FunctionCall("log", listOf(
                                FunctionCall("factorial", listOf("i".v))
                        ))),
                        AssignmentStatement("i", BinaryExpression("i".v, 1.l, Operator.ADD))
                )))
        )))
        program.eval(baseScope)
        assertEquals(listOf(1, 2, 6, 24, 120), evalLog)
    }
}
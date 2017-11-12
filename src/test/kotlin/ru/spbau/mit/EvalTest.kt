package ru.spbau.mit

import org.junit.Test
import kotlin.test.assertEquals

internal class EvalTest {
    private val evalLog = mutableListOf<Int>()

    private val baseScope = rootScopeOf(funcs = mapOf(
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
                    FunctionCall("log", 1.l),
                    FunctionCall("log", 2.l),
                    op
            )
            expr.eval(baseScope)
            assertEquals(listOf(1, 2), evalLog)
        }
    }

    @Test
    fun simpleProgram() {
        val program = Program(Block(
                VariableDefinition("x", 1.l),
                VariableDefinition("n", 1.l),
                WhileStatement(BinaryExpression("n".v, 5.l, Operator.LEQ), Block(
                        AssignmentStatement("x", BinaryExpression("x".v, "n".v, Operator.MUL)),
                        AssignmentStatement("n", BinaryExpression("n".v, 1.l, Operator.ADD))
                )),
                ExpressionStatement(FunctionCall("log", "x".v))
        ))
        program.eval(baseScope)
        assertEquals(listOf(120), evalLog)
    }

    @Test
    fun loopFactorialTest() {
        loopFactorialProgram.eval(baseScope)
        assertEquals(listOf(1, 2, 6, 24, 120), evalLog)
    }

    @Test
    fun recursionFactorialTest() {
        recursiveFactorialProgram.eval(baseScope)
        assertEquals(listOf(1, 2, 6, 24, 120), evalLog)
    }

    @Test
    fun scopeSumTest() {
        scopeSumProgram.eval(baseScope)
        assertEquals(listOf(-994, 6), evalLog)
    }
}
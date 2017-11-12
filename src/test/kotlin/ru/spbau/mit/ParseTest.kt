package ru.spbau.mit

import org.junit.Test
import kotlin.test.assertEquals

internal class ParseTest {
    @Test
    fun expressionTest1() {
        val source = "1 + 2 * (3 - 4) / (5 % 6 - 7)"
        val program = Program(Block(ExpressionStatement(
                BinaryExpression(1.l, BinaryExpression(
                        BinaryExpression(2.l, BinaryExpression(3.l, 4.l, Operator.SUB), Operator.MUL),
                        BinaryExpression(BinaryExpression(5.l, 6.l, Operator.MOD), 7.l, Operator.SUB),
                        Operator.DIV
                ), Operator.ADD)
        )))
        assertEquals(program, parse(source))
    }

    @Test
    fun expressionTest2() {
        val source = "var x = foo(bar()) + 2"
        val program = Program(Block(
                VariableDefinition("x",
                        BinaryExpression(FunctionCall("foo", FunctionCall("bar")), 2.l, Operator.ADD)
                )
        ))
        assertEquals(program, parse(source))
    }

    @Test
    fun loopFactorialTest() {
        val source = javaClass.getResource("/loopFactorial.lang").readText()
        assertEquals(loopFactorialProgram, parse(source))
    }

    @Test
    fun recursiveFactorialTest() {
        val source = javaClass.getResource("/recursiveFactorial.lang").readText()
        assertEquals(recursiveFactorialProgram, parse(source))
    }

    @Test
    fun scopeSum() {
        val source = javaClass.getResource("/scopeSum.lang").readText()
        assertEquals(scopeSumProgram, parse(source))
    }
}
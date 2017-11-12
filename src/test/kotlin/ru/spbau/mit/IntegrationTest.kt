package ru.spbau.mit

import org.junit.Test
import kotlin.test.assertEquals

class IntegrationTest {
    private val testScope = TestScope()

    @Test
    fun loopFactorialTest() {
        val program = parse(javaClass.getResource("/loopFactorial.lang").readText())
        program.eval(testScope.scope)
        assertEquals("1\n2\n6\n24\n120\n", testScope.log)
    }

    @Test
    fun recursiveFactorialTest() {
        val program = parse(javaClass.getResource("/recursiveFactorial.lang").readText())
        program.eval(testScope.scope)
        assertEquals("1\n2\n6\n24\n120\n", testScope.log)
    }

    @Test
    fun scopeSumTest() {
        val program = parse(javaClass.getResource("/scopeSum.lang").readText())
        program.eval(testScope.scope)
        assertEquals("-994\n6\n", testScope.log)
    }
}
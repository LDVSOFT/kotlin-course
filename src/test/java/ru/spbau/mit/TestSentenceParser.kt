package ru.spbau.mit

import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class TestSentenceParser {
    @Test
    fun testSamples() {
        assertNotNull(SentenceParser.parse("petr"))
        assertNull(SentenceParser.parse("etis atis animatis etis atis amatis"))
        assertNotNull(SentenceParser.parse("nataliala kataliala vetra feinites"))
    }

    @Test
    fun testSingleWord() {
        assertNotNull(SentenceParser.parse("petr"))
        assertNotNull(SentenceParser.parse("kataliala"))
        assertNotNull(SentenceParser.parse("feinites"))
    }
}
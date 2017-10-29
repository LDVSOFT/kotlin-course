package ru.spbau.mit

import org.junit.Test
import ru.spbau.mit.WordGenus.FEMALE
import ru.spbau.mit.WordGenus.MALE
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class TestWordParser {
    private fun assertKindEquals(expected: WordKind, testedWord: String) =
            assertEquals(expected, WordParser.parse(testedWord)?.kind)

    @Test
    fun testSamples() {
        assertKindEquals(Noun(MALE), "petr")

        assertNull(WordParser.parse("etis"))
        assertNull(WordParser.parse("atis"))
        assertNull(WordParser.parse("animatis"))
        assertNull(WordParser.parse("amatis"))

        assertKindEquals(Adjective(FEMALE), "nataliala")
        assertKindEquals(Adjective(FEMALE), "kataliala")
        assertKindEquals(Noun(FEMALE), "vetra")
        assertKindEquals(Verb(FEMALE), "feinites")
    }

    @Test
    fun testNouns() {
        assertKindEquals(Noun(MALE), "etr")
        assertKindEquals(Noun(MALE), "petr")

        assertKindEquals(Noun(FEMALE), "etra")
        assertKindEquals(Noun(FEMALE), "petra")

        assertNull(WordParser.parse("tr"))
        assertNull(WordParser.parse("tra"))
    }

    @Test
    fun testAdjectives() {
        assertKindEquals(Adjective(MALE), "lios")
        assertKindEquals(Adjective(MALE), "alios")

        assertKindEquals(Adjective(FEMALE), "liala")
        assertKindEquals(Adjective(FEMALE), "oliala")

        assertNull(WordParser.parse("ios"))
        assertNull(WordParser.parse("iala"))
    }

    @Test
    fun testVerbs() {
        assertKindEquals(Verb(MALE), "initis")
        assertKindEquals(Verb(MALE), "vinitis")

        assertKindEquals(Verb(FEMALE), "inites")
        assertKindEquals(Verb(FEMALE), "binites")

        assertNull(WordParser.parse("nitis"))
        assertNull(WordParser.parse("nites"))
    }

    @Test
    fun testNonWord() {
        assertNull(WordParser.parse(""))
        assertNull(WordParser.parse(" "))
        assertNull(WordParser.parse("a a"))
        assertNull(WordParser.parse("a "))
        assertNull(WordParser.parse(" a"))
        assertNull(WordParser.parse(" lios"))
    }
}

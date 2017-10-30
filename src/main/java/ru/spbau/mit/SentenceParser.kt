package ru.spbau.mit

object SentenceParser {
    fun parse(text: String): Sentence? {
        val maybeWords = text
                .split(' ')
                .map { WordParser.parse(it) }
        if (maybeWords.any { it == null })
            return null

        val words = maybeWords.map { it!! }
        if (words.size == 1)
            return SingleWordSentence(words.single())

        if (words.groupBy { it.kind.genus }.keys.size != 1) // has more than one genus
            return null

        val adjectives = words.takeWhile { it.kind is Adjective }
        val verbs = words.takeLastWhile { it.kind is Verb }
        if (adjectives.size + verbs.size + 1 != words.size)
            return null
        val noun = words[adjectives.size]
        if (noun.kind !is Noun)
            return null

        return Collocation(adjectives, noun, verbs)
    }
}
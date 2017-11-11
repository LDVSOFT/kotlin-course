package ru.spbau.mit

object SentenceParser {
    fun parse(text: String): Sentence? {
        val maybeWords = text
                .split(' ')
                .map(WordParser::parse)
        if (maybeWords.any { it == null })
            return null

        val words = maybeWords.map { it!! }
        if (words.size == 1) {
            return SingleWordSentence(words.single())
        }

        if (words.distinctBy { it.kind.genus }.size != 1) {
            return null
        }

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
package ru.spbau.mit

object WordParser {
    private val wordRegex = Regex("[a-zA-Z]*")

    private fun lookupGenus(word: String, map: Map<String, WordGenus>) = map
            .mapNotNull { (suffix, genus) -> if (word.endsWith(suffix)) genus else null }
            .singleOrNull()

    private fun <T, R> T?.map(block: (T) -> R): R? {
        if (this == null)
            return null
        return block(this)
    }

    fun parse(word: String): Word? {
        if (!word.matches(wordRegex))
            return null

        val nounGenus = lookupGenus(word, Noun.SUFFIXES).map { Noun(it) }
        val verbGenus = lookupGenus(word, Verb.SUFFIXES).map { Verb(it) }
        val adjGenus = lookupGenus(word, Adjective.SUFFIXES).map { Adjective(it) }

        val kind = listOf(nounGenus, verbGenus, adjGenus).singleOrNull { it != null } ?: return null
        return Word(word, kind)
    }
}


package ru.spbau.mit

object WordParser {
    private val WORD_REGEX = Regex("[a-zA-Z]*")

    private fun lookupGenus(word: String, map: Map<String, WordGenus>) = map
            .mapNotNull { (suffix, genus) -> genus.takeIf { word.endsWith(suffix) } }
            .singleOrNull()

    fun parse(word: String): Word? {
        if (!word.matches(WORD_REGEX))
            return null

        val nounGenus = lookupGenus(word, Noun.SUFFIXES)?.let { Noun(it) }
        val verbGenus = lookupGenus(word, Verb.SUFFIXES)?.let { Verb(it) }
        val adjGenus = lookupGenus(word, Adjective.SUFFIXES)?.let { Adjective(it) }

        val kind = listOfNotNull(nounGenus, verbGenus, adjGenus).singleOrNull() ?: return null
        return Word(word, kind)
    }
}


package ru.spbau.mit

enum class WordGenus {
    MALE,
    FEMALE
}

sealed class WordKind(
        open val genus: WordGenus
)

data class Noun(override val genus: WordGenus) : WordKind(genus) {
    companion object {
        val SUFFIXES = mapOf("etr" to WordGenus.MALE, "etra" to WordGenus.FEMALE)
    }
}

data class Adjective(override val genus: WordGenus) : WordKind(genus) {
    companion object {
        val SUFFIXES = mapOf("lios" to WordGenus.MALE, "liala" to WordGenus.FEMALE)
    }
}

data class Verb(override val genus: WordGenus) : WordKind(genus) {
    companion object {
        val SUFFIXES = mapOf("initis" to WordGenus.MALE, "inites" to WordGenus.FEMALE)
    }
}

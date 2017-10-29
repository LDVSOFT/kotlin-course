package ru.spbau.mit

sealed class Sentence

data class Collocation(
        val adjectives: List<Word>,
        val noun: Word,
        val verbs: List<Word>
) : Sentence()

data class SingleWordSentence(
        val word: Word
) : Sentence()
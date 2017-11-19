package ru.spbau.mit

data class TextElement(val text: String): TexElement {
    override fun render(to: StringBuilder, indent: String) {
        to.append(text.prependIndent(indent))
    }
}
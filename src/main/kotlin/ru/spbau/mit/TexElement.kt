package ru.spbau.mit

import java.io.OutputStream

@TexDslMarker
interface TexElement {
    fun render(to: StringBuilder, indent: String)
}

fun TexElement.toOutputStream(os: OutputStream) {
    os.bufferedWriter().use {
        it.write(StringBuilder()
                .also { render(it, "") }
                .toString())
    }
}
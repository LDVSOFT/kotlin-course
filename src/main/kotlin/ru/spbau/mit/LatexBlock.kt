package ru.spbau.mit

open class LatexBlock(val name: String, vararg val options: String) : Block() {
    protected open fun renderOpening(to: StringBuilder, indent: String) {
        to.append("$indent\\begin{$name}")
        if (options.isNotEmpty()) {
            to.append(options.joinToString(",", "[", "]"))
        }
        to.append("\n")
    }

    protected open fun renderClosing(to: StringBuilder, indent: String) {
        to.append("$indent\\end{$name}")
    }

    override fun render(to: StringBuilder, indent: String) {
        renderOpening(to, indent)
        super.render(to, indent)
        renderClosing(to, indent)
    }
}

fun LatexBlock.customTag(name: String, vararg options: String, init: LatexBlock.() -> Unit)
        = addChild(LatexBlock(name, *options), init)
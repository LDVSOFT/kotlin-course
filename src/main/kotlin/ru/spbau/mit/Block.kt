package ru.spbau.mit

open class Block: TexElement {
    companion object {
        private val BLOCK_INDENT = "  "
    }

    val children = arrayListOf<TexElement>()

    operator fun String.unaryPlus() {
        addChild(TextElement(this))
    }

    protected fun renderChilder(to: StringBuilder, indent: String) {
        for (it in children) {
            it.render(to, indent)
            to.append('\n')
        }
    }

    override fun render(to: StringBuilder, indent: String) {
        renderChilder(to, indent + BLOCK_INDENT)
    }

    fun <T: TexElement> addChild(element: T, init: T.() -> Unit = {}): T {
        children.add(element.also(init))
        return element
    }
}
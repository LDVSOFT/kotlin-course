package ru.spbau.mit

open class TexCommand(
        val name: String,
        vararg val args: String,
        val options: Array<out String> = emptyArray()
): TexElement {
    override fun render(to: StringBuilder, indent: String) {
        to.append("$indent\\$name")
        if (args.isNotEmpty()) {
            to.append(args.joinToString(",", "{", "}"))
        }
        if (options.isNotEmpty()) {
            to.append(options.joinToString(",", "[", "]"))
        }
        to.append(' ')
    }
}

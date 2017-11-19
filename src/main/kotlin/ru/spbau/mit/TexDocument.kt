package ru.spbau.mit

class TexDocument : Block() {
    override fun render(to: StringBuilder, indent: String) {
        renderChilder(to, indent)
    }
}

fun document(init: TexDocument.() -> Unit)
    = TexDocument().apply(init)

class DocumentClass(
        name: String,
        options: Array<out String>
) : TexCommand("documentclass", name, options = options)

fun TexDocument.documentClass(name: String, vararg options: String)
    = addChild(DocumentClass(name, options))

class UsePackage(
        packageName: String,
        vararg args: String
) : TexCommand("usepackage", packageName, options = args)

fun TexDocument.usePackage(packageName: String, vararg args: String)
    = addChild(UsePackage(packageName, *args))

class Document : LatexBlock("document")

fun TexDocument.document(init: Document.() -> Unit)
        = addChild(Document(), init)
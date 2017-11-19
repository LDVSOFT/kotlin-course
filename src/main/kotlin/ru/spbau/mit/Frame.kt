package ru.spbau.mit

class Frame(vararg options: String) : LatexBlock("frame", *options)

class FrameTitle(title: String) : TexCommand("frametitile", title, options = emptyArray())

fun Frame.title(title: String) = addChild(FrameTitle(title))

fun TexDocument.frame(vararg options: String, frameTitle: String? = null, init: Frame.() -> Unit)
    = addChild(Frame(*options)) {
        if (frameTitle != null) {
            title(frameTitle)
            init()
        }
    }
package ru.spbau.mit

class List(name: String): LatexBlock(name)

class Item: TexCommand("item")

class ItemBlock(item: Item) : Block() {
    init {
        addChild(item)
    }
}

fun LatexBlock.enumeration(init: List.() -> Unit)
        = addChild(List("enumerate"), init)

fun LatexBlock.itemize(init: List.() -> Unit)
        = addChild(List("itemize"), init)

fun List.item(init: ItemBlock.() -> Unit)
        = addChild(ItemBlock(Item()), init)
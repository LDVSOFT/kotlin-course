package ru.spbau.mit

typealias Function = (List<Int>) -> Int

class FunctionScope {
    var returned: Int? = null
        private set

    fun returnWith(value: Int = 0) {
        returned = value
    }
}

class Scope(
        private val parentScope: Scope?,
        val functionScope: FunctionScope,
        vars: Map<String, Int> = emptyMap(),
        funcs: Map<String, Function> = emptyMap()
) {
    private val variables: MutableMap<String, Int> = vars.toMutableMap()
    private val functions: MutableMap<String, Function> = funcs.toMutableMap()

    constructor(
            parentScope: Scope,
            vars: Map<String, Int> = emptyMap(),
            funcs: Map<String, Function> = emptyMap()
    ): this(parentScope, parentScope.functionScope, vars, funcs)

    val returned get() = functionScope.returned != null

    fun lookupAndGetVariable(name: String): Int?
        = variables[name] ?: parentScope?.lookupAndGetVariable(name)

    fun lookupAndSetVariable(name: String, value: Int): Boolean {
        if (name in variables) {
            variables[name] = value
            return true
        }
        if (parentScope?.lookupAndSetVariable(name, value) == true) {
            return true
        }
        return false
    }

    fun addVariable(name: String, value: Int?): Boolean {
        if (name in variables)
            return false
        variables[name] = value ?: 0
        return true
    }

    fun lookupAndGetFunction(name: String): Function?
        = functions[name] ?: parentScope?.lookupAndGetFunction(name)

    fun addFunction(name: String, function: Function): Boolean {
        if (name in functions)
            return false
        functions[name] = function
        return true
    }
}
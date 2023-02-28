package priv.alex.parser.engine.lr

import priv.alex.parser.Symbol

data class LRAction(val action: Action, val actionTarget: Int) {
    override fun toString(): String {
        val result = StringBuilder()
        result.append(action).append(" ").append(actionTarget)
        return result.toString()
    }
}


enum class Action {
    SHIFT,
    REDUCE,
    ACCEPT
}

class LRTable {
    private val actions = HashMap<Pair<Int, Symbol>, LRAction>()
    private val goto = HashMap<Symbol, Int>()

    fun goto(symbol: Symbol): Int? {
        return goto[symbol]
    }

    fun addGoto(symbol: Symbol, state: Int) {
        goto[symbol] = state
    }

    fun addAction(state: Pair<Int, Symbol>, action: LRAction) {
        actions[state] = action
    }

    fun action(state: Pair<Int, Symbol>): LRAction? {
        return actions[state]
    }

}

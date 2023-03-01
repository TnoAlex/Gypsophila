package priv.alex.parser.engine.lr

import priv.alex.lexer.token.Token
import priv.alex.lexer.token.TokenType
import priv.alex.parser.EOF
import priv.alex.parser.Symbol
import priv.alex.parser.Terminator

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

    fun action(state: Int, token: Token?): LRAction? {
        if (token == null) {
            return actions[Pair(state, EOF())]
        } else {
            var symbol = Terminator(TokenType.stringOf(token.type))
            actions[Pair(state, symbol)]?.let { return it } ?: let {
                symbol = Terminator(token.name)
                actions[Pair(state, symbol)]?.let { a -> return a } ?: let {
                    symbol = Terminator(token.rawText)
                    actions[Pair(state, symbol)]?.let { a -> return a } ?: let {
                        return null
                    }
                }
            }
        }
    }

}

package priv.alex.parser.engine.lr

import priv.alex.core.NoArg
import priv.alex.lexer.token.Token
import priv.alex.lexer.token.TokenType
import priv.alex.parser.EOF
import priv.alex.parser.Symbol
import priv.alex.parser.Terminator
import java.io.Serializable

@NoArg
data class LRAction(val action: Action, val actionTarget: Int) : Serializable {
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

class LRTable : Serializable {
    private val actions = HashMap<Pair<Int, Symbol>, LRAction>()
    private val goto = HashMap<Pair<Int, Symbol>, Int>()

    fun goto(state: Int, symbol: Symbol): Int? {
        return goto[Pair(state, symbol)]
    }

    fun addGoto(state: Pair<Int, Symbol>, target: Int) {
        goto[state] = target
    }

    fun addAction(state: Pair<Int, Symbol>, action: LRAction) {
        actions[state] ?: actions.put(state, action)
    }

    fun action(state: Int, token: Token?): Pair<LRAction, Symbol>? {
        if (token == null) {
            actions[Pair(state, EOF())]?.let { return Pair(it, EOF()) } ?: let { return null }
        } else {
            var symbol = Terminator(TokenType.stringOf(token.type))
            actions[Pair(state, symbol)]?.let { return Pair(it, symbol) } ?: let {
                symbol = Terminator(token.name)
                actions[Pair(state, symbol)]?.let { a -> return Pair(a, symbol) } ?: let {
                    symbol = Terminator(token.rawText)
                    actions[Pair(state, symbol)]?.let { a -> return Pair(a, symbol) } ?: let {
                        return null
                    }
                }
            }
        }
    }

}

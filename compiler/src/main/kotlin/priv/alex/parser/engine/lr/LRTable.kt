package priv.alex.parser.engine.lr

import priv.alex.core.NoArg
import priv.alex.lexer.token.Token
import priv.alex.lexer.token.TokenType
import priv.alex.parser.EOF
import priv.alex.parser.Symbol
import priv.alex.parser.Terminator
import java.io.Serializable

/**
 * Lr action
 *
 * @property action
 * @property actionTarget
 * @constructor Create empty L r action
 */
@NoArg
data class LRAction(val action: Action, val actionTarget: Int) : Serializable {
    override fun toString(): String {
        val result = StringBuilder()
        result.append(action).append(" ").append(actionTarget)
        return result.toString()
    }
}

/**
 * Action
 *
 * @constructor Create Action
 */
enum class Action {
    SHIFT,
    REDUCE,
    ACCEPT
}

/**
 * Lr table
 *
 * @constructor Create Lr table
 */
class LRTable : Serializable {
    private val actions = HashMap<Pair<Int, Symbol>, LRAction>()
    private val goto = HashMap<Pair<Int, Symbol>, Int>()

    /**
     * Goto
     *
     * @param state CanonicalCluster state
     * @param symbol accept symbol
     * @return target CanonicalCluster id
     */
    fun goto(state: Int, symbol: Symbol): Int? {
        return goto[Pair(state, symbol)]
    }

    /**
     * Add goto
     *
     * @param state CanonicalCluster state
     * @param target CanonicalCluster state
     */
    fun addGoto(state: Pair<Int, Symbol>, target: Int) {
        goto[state] = target
    }

    /**
     * Add action
     *
     * @param state CanonicalCluster state
     * @param action LR action
     */
    fun addAction(state: Pair<Int, Symbol>, action: LRAction) {
        actions[state] ?: actions.put(state, action)
    }

    /**
     * Action
     *
     * @param state CanonicalCluster state
     * @param token current Token
     * @return
     */
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

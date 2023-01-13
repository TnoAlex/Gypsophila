package priv.alex.lexer.token

import priv.alex.lexer.engine.fsm.DFA
import java.util.UUID

class TokenFactory(val identify: String, private val regex: String) {

    private val factoryId: String = UUID.randomUUID().toString()

    private val dfa: DFA = DFA().buildDFA(regex)

    fun buildToken(rawCode: String): ArrayList<Token> {
        val identifiedToken = ArrayList<Token>()

        return identifiedToken
    }
}
package priv.alex.lexer

import priv.alex.core.NoArg
import priv.alex.lexer.engine.automaton.DFA
import priv.alex.lexer.token.TokenType

/**
 * Lexical
 *
 * @property type Token Type
 * @property name Token name
 * @property value Token value
 * @property regex Automata for identifying tokens
 * @constructor Create Lexical
 */
@NoArg
data class Lexical(val type: TokenType, val name: String, val value: String?, val regex: String?){

    val dfa:DFA? = if(regex == null)
        null
    else
        DFA(regex)
}

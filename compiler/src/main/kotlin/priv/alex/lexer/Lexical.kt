package priv.alex.lexer

import priv.alex.lexer.engine.automaton.DFA
import priv.alex.lexer.token.TokenType
import priv.alex.noarg.NoArg

@NoArg
data class Lexical(val type: TokenType, val name: String, val value: String?, val regex: String?){

    val dfa:DFA? = if(regex == null)
        null
    else
        DFA(regex)
}

package priv.alex.lexer.token

import priv.alex.lexer.engine.fsm.DFA

data class Token(
    val type: TokenType,
    val rawText: String,
    val textRange: Pair<Int, Int>
)
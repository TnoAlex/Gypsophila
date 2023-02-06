package priv.alex.lexer

import priv.alex.lexer.token.TokenType
import priv.alex.noarg.NoArg

@NoArg
data class Lexical(val type: TokenType, val name: String, val regex: String)

package priv.alex.lexer

import priv.alex.lexer.token.TokenType
import priv.alex.noarg.NoArg

@NoArg
data class Lexicon(val type: TokenType, val lexical : ArrayList<Lexical>)
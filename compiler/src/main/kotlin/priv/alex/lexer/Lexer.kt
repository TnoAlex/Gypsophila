package priv.alex.lexer

import priv.alex.noarg.NoArg

@NoArg
data class Lexer(val type: String,val regex : ArrayList<String>)
{
}
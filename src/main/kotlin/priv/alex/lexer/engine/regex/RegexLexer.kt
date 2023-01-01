package priv.alex.lexer.engine.regex

class RegexLexer(private val pattern: String) {

    private var position: Int = 0


    fun advance(): RegexToken {
        if (position > pattern.lastIndex)
            return RegexToken(RegexTokenEnum.EOF)
        return if (pattern[position] == '\\') {
            position += 1
            handleEscape()
        } else
            RegexToken.getToken(pattern[position].toString())
    }

    private fun handleEscape(): RegexToken {
        val pos = position
        position += 1
        return when (pattern[pos]) {
            'b' -> RegexToken("\u0008")
            't' -> RegexToken(" \u0009")
            'r' -> RegexToken("\u000D")
            'n' -> RegexToken("\u000A")
            'e' -> RegexToken("\u001B")
            else -> RegexToken(pattern[pos].toString())
        }

    }
}
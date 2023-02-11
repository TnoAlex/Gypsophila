package priv.alex.lexer.engine.regex

class RegexLexer(private val pattern: String) {

    private var position: Int = 0
    var currentToken:RegexToken = RegexToken(RegexTokenEnum.L)
        private set


    fun advance(): RegexToken {
        if (position == pattern.lastIndex){
            currentToken = RegexToken(RegexTokenEnum.EOF)
            position++
            return currentToken
        }
        if(position > pattern.lastIndex)
            throw RuntimeException("The pointer is out of bounds but the NFA build is not complete, there may be a problem with this regularity: $pattern")
        return if (pattern[position] == '\\') {
            position += 1
            currentToken = handleEscape()
            currentToken
        } else{
            position+=1
            currentToken = RegexToken.getToken(pattern[position-1].toString())
            currentToken
        }

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
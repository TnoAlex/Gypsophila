package priv.alex.parser

class NonTerminator(content: String) : Symbol(content) {
    init {
        check(isNonTerminator(content)) { "Incomprehensible terminator" }
    }


    companion object {
        fun isNonTerminator(string: String): Boolean {
            if (string.startsWith('<') && string.endsWith('>'))
                return true
            return false
        }
    }
}
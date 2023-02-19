package priv.alex.parser

class Terminator(content: String) : Symbol(content) {
    init {
        check(isTerminator(content)) { "Incomprehensible terminator" }
    }

    companion object{
        fun isTerminator(string: String): Boolean {
            if (string.startsWith('<') && string.endsWith('>'))
                return true
            return false
        }
    }
}

package priv.alex.parser

import java.io.Serializable

/**
 * NonTerminator
 *
 * @constructor
 *
 * @param content content of NonTerminator
 */
class NonTerminator(content: String) : Symbol(content),Serializable{
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
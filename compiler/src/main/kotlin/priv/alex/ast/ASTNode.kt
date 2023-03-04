package priv.alex.ast

import priv.alex.lexer.token.Token
import priv.alex.lexer.token.TokenType
import priv.alex.parser.Symbol

data class ASTNode(val value: Pair<Symbol, Token?>) {

    private val nodeId: Long = System.currentTimeMillis()

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        other as ASTNode
        if (other.value != value) return false
        return true
    }

    override fun toString(): String {
        val builder = StringBuilder()
        value.second?.let {
            if (it.type == TokenType.LITERAL) {
                builder.append(it.rawText)
            } else {
                builder.append(TokenType.stringOf(it.type))
            }
        } ?: let { builder.append(value.first.content) }
        return builder.toString()
    }

    override fun hashCode(): Int {
        return (value.toString() + nodeId).hashCode()
    }

}

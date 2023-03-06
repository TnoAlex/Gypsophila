package priv.alex.ast

import priv.alex.lexer.token.Token
import priv.alex.parser.NonTerminator
import priv.alex.parser.Symbol
import java.util.*

data class ASTNode(val value: Pair<Symbol, Token?>) {

    private val nodeId: String = UUID.randomUUID().toString()

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        other as ASTNode
        if (other.value != value) return false
        if (nodeId != other.nodeId) return false
        return true
    }

    override fun toString(): String {
        if (value.first is NonTerminator) {
            return value.first.toString()
        } else {
            return value.second!!.type.toString() + " : " + value.second!!.rawText
        }
    }

    override fun hashCode(): Int {
        return nodeId.hashCode()
    }

}

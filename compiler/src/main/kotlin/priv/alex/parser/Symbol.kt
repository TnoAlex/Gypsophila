package priv.alex.parser

/**
 * Symbol
 * The super class of NonTerminator, Terminator , EOF and EmptySymbol
 * @property content The content of Symbol
 * @constructor Create  Symbol
 */
abstract class Symbol(val content: String) {

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        other as Symbol
        return content == other.content
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }

    override fun toString(): String {
        return content
    }
}
